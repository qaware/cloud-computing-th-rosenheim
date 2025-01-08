### Searches for an existing role named `BasicLambdaExecutionRole`
data aws_iam_role iam_for_lambda {
  name = "BasicLambdaExecutionRole"
}

### Terraform's way of packaging files into an archive - in this case we are packaging the JS source code
### for the lambda function.
data "archive_file" "lambda" {
  type        = "zip"
  source_file = "hello.mjs"
  output_path = "lambda_function_payload.zip"
}

### Creates the lambda function
resource "aws_lambda_function" "hello_world" {
  filename      = "lambda_function_payload.zip"
  function_name = "" ### TODO enter a unique name here
  # references the role, that is queried above
  role          = data.aws_iam_role.iam_for_lambda.arn
  # reference into the js file, format is ${filename}.{export-name}
  handler       = "hello.handler"

  source_code_hash = data.archive_file.lambda.output_base64sha256

  runtime = "nodejs20.x"
}

### Creates a cloudwatch log group for the lambda.
### While not referenced anywhere else, the lambda function will log in there based on its name.
resource "aws_cloudwatch_log_group" "hello_world" {
  name = "/aws/lambda/${aws_lambda_function.hello_world.function_name}"

  retention_in_days = 7
}