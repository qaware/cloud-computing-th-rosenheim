# ToDo: Create a Security Group for the Load Balancer
# See: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group

# ToDo: Allow incoming traffic on the Load Balancer on port 80 from everywhere
# See: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule

# ToDo: Allow outgoing traffic from the LB to everywhere
# See: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group_rule

# ToDo: Create the Load Balancer
# See: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb

# resource "aws_lb" "app" {
#   name               =
#   load_balancer_type = "application"
#   security_groups    =
#   subnets            =
#
#   tags = local.standard_tags
# }

# ToDo: Create a target group
# See: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group

# resource "aws_lb_target_group" "app" {
#   name     =
#   port     =
#   protocol =
#   vpc_id   =
#
#   health_check {
#     healthy_threshold   =
#     unhealthy_threshold =
#     interval            =
#     path                =
#     port                =
#     protocol            =
#   }
#
#   tags = local.standard_tags
# }

# ToDo: Create a listener
# See: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener

# resource "aws_lb_listener" "lb_forward_to_app" {
#   ...
#   default_action {
#     ...
#   }
#
#   tags = local.standard_tags
# }
