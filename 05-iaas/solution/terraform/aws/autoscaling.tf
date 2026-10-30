data "aws_ssm_parameter" "al2023_ami" {
  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"
}

resource "aws_security_group" "app" {
  name        = "${local.env}-app"
  description = "Security Group for the App-Instances"
  vpc_id      = module.vpc.vpc_id
  tags        = local.standard_tags
}

resource "aws_vpc_security_group_ingress_rule" "app_ingress_ssh_all" {
  #checkov:skip=CKV_AWS_24:Ensure no security groups allow ingress from 0.0.0.0:0 to port 22

  security_group_id = aws_security_group.app.id
  description       = "Allows SSH from everywhere"
  from_port         = 22
  to_port           = 22
  ip_protocol       = "tcp"
  cidr_ipv4         = "0.0.0.0/0"
  tags              = local.standard_tags
}

resource "aws_vpc_security_group_ingress_rule" "app_ingress_http_lb" {
  security_group_id            = aws_security_group.app.id
  description                  = "Allows HTTP access from the load balancer"
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.lb.id
  tags                         = local.standard_tags
}

resource "aws_vpc_security_group_egress_rule" "app_egress_all" {
  security_group_id = aws_security_group.app.id
  description       = "Allows all outgoing traffic"
  ip_protocol       = "-1"
  cidr_ipv4         = "0.0.0.0/0"
  tags              = local.standard_tags
}

resource "aws_launch_template" "app" {
  #checkov:skip=CKV_AWS_88:EC2 instance should not have public IP

  name                                 = local.env
  image_id                             = data.aws_ssm_parameter.al2023_ami.value
  instance_initiated_shutdown_behavior = "terminate"
  update_default_version               = true
  instance_type                        = "t2.micro"

  network_interfaces {
    associate_public_ip_address = true
    security_groups             = [aws_security_group.app.id]
  }

  tag_specifications {
    resource_type = "instance"
    tags          = local.standard_tags
  }

  metadata_options {
    http_endpoint          = "enabled"
    http_tokens            = "required"
    instance_metadata_tags = "enabled"
  }

  user_data = base64encode(
    templatefile(
      "${path.module}/init.sh", { message = local.config.message }
    )
  )
}

resource "aws_autoscaling_group" "app" {
  name                = local.env
  min_size            = 0
  max_size            = 4
  desired_capacity    = 1
  vpc_zone_identifier = module.vpc.public_subnets
  health_check_type   = "ELB"
  target_group_arns   = [aws_lb_target_group.app.arn]

  launch_template {
    id = aws_launch_template.app.id
  }

  dynamic "tag" {
    for_each = local.standard_tags_asg
    content {
      key                 = tag.value.key
      propagate_at_launch = tag.value.propagate_at_launch
      value               = tag.value.value
    }
  }
}
