output "load_balancer_url" {
  description = "The URL of the Load Balancer"
  value       = "http://${aws_lb.app.dns_name}"
}
