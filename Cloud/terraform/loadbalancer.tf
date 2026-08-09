resource "aws_lb" "backend" {
  name               = "${var.project}-backend-nlb"
  internal           = true
  load_balancer_type = "network"
  subnets            = [aws_subnet.private.id]
  security_groups    = [aws_security_group.nlb.id]

  tags = { Name = "${var.project}-backend-nlb" }
}

resource "aws_lb_target_group" "backend" {
  name        = "${var.project}-backend-tg"
  port        = 8080
  protocol    = "TCP"
  vpc_id      = aws_vpc.main.id
  target_type = "instance"

  health_check {
    protocol = "TCP"
    port     = "traffic-port"
  }

  tags = { Name = "${var.project}-backend-tg" }
}

resource "aws_lb_target_group_attachment" "backend" {
  target_group_arn = aws_lb_target_group.backend.arn
  target_id        = aws_instance.backend.id
  port             = 8080
}

resource "aws_lb_listener" "backend" {
  load_balancer_arn = aws_lb.backend.arn
  port              = 8080
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }
}
