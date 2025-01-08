output "function_name" {
  description = "Name of the Lambda function."

  value = aws_lambda_function.hello_world.function_name
}

output "gateway_domain" {
  description = "Host of the provisioned API gateway"

  value = aws_apigatewayv2_api.lambda.api_endpoint
}