resource "aws_security_group" "frontend" {
  name        = "${var.project}-frontend-sg"
  description = "Frontend EC2 - HTTP inbound, SSH for management"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.ssh_ingress_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project}-frontend-sg" }
}

resource "aws_security_group" "nlb" {
  name        = "${var.project}-nlb-sg"
  description = "NLB - reachable from within the VPC for the API Gateway VPC Link"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "TCP 8080 from VPC"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project}-nlb-sg" }
}

resource "aws_security_group" "backend" {
  name        = "${var.project}-backend-sg"
  description = "Backend EC2 - only reachable from the NLB"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "TCP 8080 from NLB"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.nlb.id]
  }

  ingress {
    description = "SSH from the frontend jump host"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    security_groups = [aws_security_group.frontend.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project}-backend-sg" }
}

resource "aws_security_group" "nat" {
  name        = "${var.project}-nat-sg"
  description = "NAT instance - all traffic from the private subnets"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "All traffic from private subnets"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = [var.private_subnet_cidr, var.private_subnet_2_cidr]
  }
  ingress {
    description     = "SSH from the frontend jump host"
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [aws_security_group.frontend.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project}-nat-sg" }
}

resource "aws_security_group" "rds" {
  name        = "${var.project}-rds-sg"
  description = "Postgres RDS - only reachable from the backend"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Postgres from backend"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.backend.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project}-rds-sg" }
}
