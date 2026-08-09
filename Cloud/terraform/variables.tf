variable "aws_region" {
  description = "AWS region for all resources"
  type        = string
  default     = "us-east-1"
}

variable "project" {
  description = "Prefix used for naming resources (matches your existing 'stb' naming)"
  type        = string
  default     = "stb"
}

# --- Networking ---

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "public_subnet_cidr" {
  type    = string
  default = "10.0.1.0/24"
}

variable "private_subnet_cidr" {
  type    = string
  default = "10.0.2.0/24"
}

variable "private_subnet_2_cidr" {
  description = "Second private subnet, required by RDS for a multi-AZ DB subnet group"
  type        = string
  default     = "10.0.3.0/24"
}

variable "az_a" {
  type    = string
  default = "us-east-1a"
}

variable "az_b" {
  type    = string
  default = "us-east-1b"
}

# --- Access ---

variable "key_name" {
  description = "Existing EC2 key pair name (create this manually first, e.g. 'projecy' - Terraform does not manage the .pem file)"
  type        = string
  default     = "projecy"
}

variable "ssh_ingress_cidr" {
  description = "CIDR allowed to SSH into the frontend instance. Narrow this to your IP/32 when possible."
  type        = string
  default     = "0.0.0.0/0"
}

variable "instance_profile_name" {
  description = "Existing IAM instance profile granting ECR pull access (e.g. Learner Lab's 'LabInstanceProfile'). Leave null to skip attaching one."
  type        = string
  default     = null
}

# --- Compute ---

variable "frontend_instance_type" {
  type    = string
  default = "t2.micro"
}

variable "backend_instance_type" {
  type    = string
  default = "t2.micro"
}

variable "nat_instance_type" {
  type    = string
  default = "t2.micro"
}

# --- Database ---

variable "db_name" {
  type    = string
  default = "stbdb"
}

variable "db_username" {
  type    = string
  default = "stbuser"
}

variable "db_password" {
  description = "RDS master password. Pass via terraform.tfvars (gitignored) or TF_VAR_db_password env var - never commit this."
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  type    = string
  default = "db.t3.micro"
}

variable "backend_image_tag" {
  description = "Tag to deploy for stb-backend"
  type        = string
  default     = "latest"
}

variable "frontend_image_tag" {
  description = "Tag to deploy for stb-frontend"
  type        = string
  default     = "latest"
}
