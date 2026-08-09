# STB banking app - Terraform

Recreates everything you built by hand in the console: VPC, public/private
subnets, self-managed NAT instance, security groups, an internal NLB,
API Gateway (HTTP API) with a VPC Link, two EC2 instances (frontend/backend)
that pull their images from ECR on boot, ECR repositories, and an RDS
Postgres database.

## Before you run this

1. **Create the key pair manually first** (Terraform doesn't manage the
   `.pem` file itself, only which key name gets attached):
   ```
   aws ec2 create-key-pair --key-name projecy --query 'KeyMaterial' --output text > projecy.pem
   chmod 400 projecy.pem
   ```
   If you already have `projecy` from your manual setup, skip this - just
   make sure `key_name` in your tfvars matches it exactly.

2. **Build and push your Docker images to ECR at least once** before or
   right after `apply`. The EC2 user-data scripts pull `:latest` (or
   whatever tag you set) on first boot - if the ECR repo is empty, the
   instance will come up with no running container until you push an image
   and rerun `terraform apply -replace=aws_instance.backend` (or just
   SSH in and re-pull manually, same as before).

3. **Check whether your Learner Lab already has an instance profile** with
   ECR pull permissions (commonly `LabInstanceProfile`). If so, set
   `instance_profile_name` in your tfvars - without it, the `aws ecr
   get-login-password` call inside user-data will fail on boot because the
   instance has no credentials.

4. Copy the example vars file and fill in your real password:
   ```
   cp terraform.tfvars.example terraform.tfvars
   ```
   `terraform.tfvars` is where secrets like `db_password` live - do not
   commit it.

## Usage

```
terraform init
terraform plan
terraform apply
```

Takes roughly 10-15 minutes, mostly waiting on the RDS instance to become
available (backend instance won't finish its user-data cleanly until RDS
is up, since Terraform passes the RDS endpoint into the backend's
user-data - that dependency is wired in automatically).

## After apply

```
terraform output
```
Gives you the frontend IP, backend private IP, API Gateway invoke URL,
RDS endpoint, and both ECR repo URLs in one place - no more hunting
through the console for the invoke URL.

## Learner Lab specific notes

- Sessions rotate credentials every restart. Terraform state itself isn't
  affected, but if a `plan`/`apply` fails with an auth error, refresh your
  credentials the same way you have been (Start Lab → AWS Details → update
  `~/.aws/credentials`) and retry.
- If `rds:CreateDBInstance` or similar comes back `AccessDenied`, your
  lab's IAM policy restricts that service - this is a hard limit of the
  lab, not something fixable in Terraform.
- **Persistence across lab resets is the whole point of this file** - once
  applied, `terraform apply` again after a session restart will detect
  what's already there and only recreate what's actually missing (e.g. if
  the lab wiped EC2 instances but kept the VPC), rather than you rebuilding
  everything by hand again.

## What's intentionally different from your manual setup

- **Two private subnets** instead of one - RDS requires a subnet group
  spanning 2+ AZs even for a single-AZ database. `stb-private-subnet-2`
  (`us-east-1b`) is new; only the DB and its route table live there.
- **Frontend now has a stable Elastic IP** by default instead of relying
  on subnet auto-assign, so the API Gateway CORS origin and your bookmarks
  don't shift every time the instance is replaced.
- **Docker port mapping and `--restart unless-stopped` are baked into
  user-data** for both frontend and backend, which is the fix for the
  "empty PORTS column" issue you hit earlier - you shouldn't need to type
  the `docker run -p ...` command by hand again after this.
