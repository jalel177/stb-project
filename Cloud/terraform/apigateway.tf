resource "aws_apigatewayv2_api" "backend" {
  name          = "${var.project}-backend-api"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins     = ["http://${aws_eip.frontend.public_ip}"]
    allow_methods     = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    allow_headers     = ["Content-Type", "Authorization"]
    allow_credentials = true
  }
}

resource "aws_apigatewayv2_vpc_link" "backend" {
  name               = "${var.project}-backend-vpc-link"
  security_group_ids = [aws_security_group.nlb.id]
  subnet_ids         = [aws_subnet.private.id]
}

resource "aws_apigatewayv2_integration" "backend" {
  api_id             = aws_apigatewayv2_api.backend.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = aws_lb_listener.backend.arn
  integration_method = "ANY"
  connection_type    = "VPC_LINK"
  connection_id      = aws_apigatewayv2_vpc_link.backend.id
}

resource "aws_apigatewayv2_route" "default" {
  api_id    = aws_apigatewayv2_api.backend.id
  route_key = "$default"
  target    = "integrations/${aws_apigatewayv2_integration.backend.id}"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.backend.id
  name        = "$default"
  auto_deploy = true
}

