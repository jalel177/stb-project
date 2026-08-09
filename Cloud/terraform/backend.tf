terraform {
  backend "s3" {
    bucket         = "stb-terraform-state-705809089341"
    key            = "stb-project/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "stb-terraform-locks"
    encrypt        = true
  }
}