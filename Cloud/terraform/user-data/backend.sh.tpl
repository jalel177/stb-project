#!/bin/bash
dnf install -y docker
systemctl enable docker
systemctl start docker

aws ecr get-login-password --region ${aws_region} | docker login --username AWS --password-stdin ${ecr_registry}

docker pull ${backend_image}

docker rm -f stb-backend 2>/dev/null || true

docker run -d \
  --name stb-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://${db_endpoint}/${db_name} \
  -e SPRING_DATASOURCE_USERNAME=${db_username} \
  -e SPRING_DATASOURCE_PASSWORD=${db_password} \
  -e APP_CORS_ALLOWED_ORIGINS=${cors_allowed_origins} \
  --restart unless-stopped \
  ${backend_image}
