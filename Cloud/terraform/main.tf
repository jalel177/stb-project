



data "aws_caller_identity" "current" {}

# Latest Amazon Linux 2023 AMI, used for all three EC2 instances
data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023*-x86_64"]
  }
  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

locals {
  ecr_registry     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.aws_region}.amazonaws.com"
  frontend_image   = "${local.ecr_registry}/${var.project}-frontend:${var.frontend_image_tag}"
  backend_image    = "${local.ecr_registry}/${var.project}-backend:${var.backend_image_tag}"
}
