#!/bin/bash
dnf install -y docker
systemctl enable docker
systemctl start docker

aws ecr get-login-password --region ${aws_region} | docker login --username AWS --password-stdin ${ecr_registry}

docker pull ${frontend_image}

docker rm -f stb-frontend 2>/dev/null || true

docker run -d \
  --name stb-frontend \
  -p 80:80 \
  -e API_URL=${api_invoke_url} \
  --restart unless-stopped \
  ${frontend_image}
