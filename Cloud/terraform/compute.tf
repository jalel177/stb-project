resource "aws_ecr_repository" "frontend" {
  name         = "${var.project}-frontend"
  force_delete = true
    lifecycle {
    prevent_destroy = true
  }
}

resource "aws_ecr_repository" "backend" {
  name         = "${var.project}-backend"
  force_delete = true
    lifecycle {
    prevent_destroy = true
  }
}


resource "aws_instance" "nat" {
  ami                         = data.aws_ami.al2023.id
  instance_type               = var.nat_instance_type
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.nat.id]
  key_name                    = var.key_name
  source_dest_check           = false
  associate_public_ip_address = true
  user_data                   = file("${path.module}/user-data/nat.sh")

  tags = { Name = "${var.project}-nat-instance" }
}

resource "aws_eip" "nat" {
  instance = aws_instance.nat.id
  domain   = "vpc"
  tags     = { Name = "${var.project}-nat-eip" }
}


resource "aws_instance" "frontend" {
  ami                         = data.aws_ami.al2023.id
  instance_type               = var.frontend_instance_type
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.frontend.id]
  key_name                    = var.key_name
  associate_public_ip_address = true
  iam_instance_profile        = var.instance_profile_name

  user_data = templatefile("${path.module}/user-data/frontend.sh.tpl", {
    aws_region     = var.aws_region
    ecr_registry   = local.ecr_registry
    frontend_image = local.frontend_image
    api_invoke_url  = aws_apigatewayv2_api.backend.api_endpoint
  })

  tags = { Name = "${var.project}-frontend" }

  depends_on = [aws_ecr_repository.frontend]
}

resource "aws_eip" "frontend" {
  domain = "vpc"
  tags   = { Name = "${var.project}-frontend-eip" }
}

resource "aws_eip_association" "frontend" {
  instance_id   = aws_instance.frontend.id
  allocation_id = aws_eip.frontend.id
}



resource "aws_instance" "backend" {
  ami                     = data.aws_ami.al2023.id
  instance_type           = var.backend_instance_type
  subnet_id               = aws_subnet.private.id
  vpc_security_group_ids  = [aws_security_group.backend.id]
  key_name                = var.key_name
  iam_instance_profile    = var.instance_profile_name

  user_data = templatefile("${path.module}/user-data/backend.sh.tpl", {
    aws_region    = var.aws_region
    ecr_registry  = local.ecr_registry
    backend_image = local.backend_image
    db_endpoint   = aws_db_instance.main.endpoint
    db_name       = var.db_name
    db_username   = var.db_username
    db_password   = var.db_password
     cors_allowed_origins  = "http://${aws_eip.frontend.public_ip}"
  })

  tags = { Name = "${var.project}-backend" }

  depends_on = [aws_ecr_repository.backend, aws_db_instance.main,aws_eip.frontend]
}
