output "frontend_public_ip" {
  value = aws_eip.frontend.public_ip
}

output "backend_private_ip" {
  value = aws_instance.backend.private_ip
}

output "nat_public_ip" {
  value = aws_eip.nat.public_ip
}

output "api_invoke_url" {
  value = aws_apigatewayv2_api.backend.api_endpoint
}

output "rds_endpoint" {
  value = aws_db_instance.main.endpoint
}

output "ecr_frontend_url" {
  value = aws_ecr_repository.frontend.repository_url
}

output "ecr_backend_url" {
  value = aws_ecr_repository.backend.repository_url
}