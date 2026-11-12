### Packages the source code of the URL shortener
data "archive_file" "links" {
  type        = "zip"
  source_file = "links.mjs"
  output_path = "links_function_payload.zip"
}

### Creates the lambda function for the URL shortener
resource "aws_lambda_function" "links" {
  filename      = "links_function_payload.zip"
  function_name = "${var.name}-links"
  # In addition to logging, this role allows reading and writing items in DynamoDB, see `iam.tf`
  role    = local.links_role_arn
  handler = "links.handler"

  source_code_hash = data.archive_file.links.output_base64sha256

  runtime = "nodejs24.x"

  # The function must not hard-code the table name - we hand it over as an environment variable.
  environment {
    variables = {
      TABLE_NAME = aws_dynamodb_table.links.name
    }
  }

  # Own account only: make sure the role has its permissions before the function exists
  depends_on = [aws_iam_role_policy_attachment.links_logs, aws_iam_role_policy.links_dynamodb]
}

resource "aws_cloudwatch_log_group" "links" {
  name = "/aws/lambda/${aws_lambda_function.links.function_name}"

  retention_in_days = 7
}

### Adds the links function as a second integration to the existing API
resource "aws_apigatewayv2_integration" "links" {
  api_id = aws_apigatewayv2_api.lambda.id

  integration_uri    = aws_lambda_function.links.invoke_arn
  integration_type   = "AWS_PROXY"
  integration_method = "POST"

  # Version 2.0 of the event format contains `routeKey`, which our function uses to decide what to do.
  payload_format_version = "2.0"
}

### POST /links - creates a new short link
resource "aws_apigatewayv2_route" "create_link" {
  api_id = aws_apigatewayv2_api.lambda.id

  route_key = "POST /links"
  target    = "integrations/${aws_apigatewayv2_integration.links.id}"
}

### GET /{code} - redirects to the stored URL.
### `{code}` is a path parameter. More specific routes like GET /hello still take precedence.
resource "aws_apigatewayv2_route" "follow_link" {
  api_id = aws_apigatewayv2_api.lambda.id

  route_key = "GET /{code}"
  target    = "integrations/${aws_apigatewayv2_integration.links.id}"
}

### Grants the api gateway permissions to invoke the links function
resource "aws_lambda_permission" "api_gw_links" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.links.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_apigatewayv2_api.lambda.execution_arn}/*/*"
}
