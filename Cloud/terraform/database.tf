resource "aws_db_subnet_group" "main" {
  name       = "${var.project}-db-subnet-group"
  subnet_ids = [aws_subnet.private.id, aws_subnet.private_2.id]

  tags = { Name = "${var.project}-db-subnet-group" }
}

resource "aws_db_instance" "main" {
  identifier             = "${var.project}-postgres"
  engine                 = "postgres"
  engine_version         = "16"
  instance_class         = var.db_instance_class
  allocated_storage      = 20
  storage_type           = "gp2"
  db_name                = var.db_name
  username               = var.db_username
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false
  skip_final_snapshot    = true
  multi_az               = false

  tags = { Name = "${var.project}-postgres" }
}
