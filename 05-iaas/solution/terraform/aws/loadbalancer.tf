resource "aws_security_group" "lb" {
  name        = "${local.env}-lb"
  description = "Security Group for the Load Balancer"
  vpc_id      = module.vpc.vpc_id

  tags = local.standard_tags
}

resource "aws_vpc_security_group_ingress_rule" "lb_ingress_http_all" {
  #checkov:skip=CKV_AWS_260:Ensure no security groups allow ingress from 0.0.0.0:0 to port 80

  security_group_id = aws_security_group.lb.id
  description       = "Allows HTTP from everywhere"
  from_port         = 80
  to_port           = 80
  ip_protocol       = "tcp"
  cidr_ipv4         = "0.0.0.0/0"
  tags              = local.standard_tags
}

resource "aws_vpc_security_group_egress_rule" "lb_egress_all" {
  security_group_id = aws_security_group.lb.id
  description       = "Allows all outgoing traffic"
  ip_protocol       = "-1"
  cidr_ipv4         = "0.0.0.0/0"
  tags              = local.standard_tags
}

resource "aws_lb" "app" {
  #checkov:skip=CKV_AWS_91:Ensure the ELBv2 (Application/Network) has access logging enabled
  #checkov:skip=CKV_AWS_150:Ensure that Load Balancer has deletion protection enabled

  name                       = local.env
  load_balancer_type         = "application"
  security_groups            = [aws_security_group.lb.id]
  subnets                    = module.vpc.public_subnets
  drop_invalid_header_fields = true

  tags = local.standard_tags
}

resource "aws_lb_target_group" "app" {
  name     = local.env
  port     = 8080
  protocol = "HTTP"
  vpc_id   = module.vpc.vpc_id

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 2
    interval            = 10
    path                = "/"
    port                = "traffic-port"
    protocol            = "HTTP"
  }

  tags = local.standard_tags
}

resource "aws_lb_listener" "lb_forward_to_app" {
  #checkov:skip=CKV_AWS_2:Ensure ALB protocol is HTTPS

  load_balancer_arn = aws_lb.app.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }

  tags = local.standard_tags
}
