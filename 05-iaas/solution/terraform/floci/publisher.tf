module "lambda_publisher" {
  # https://github.com/terraform-aws-modules/terraform-aws-lambda/blob/master/CHANGELOG.md
  source  = "terraform-aws-modules/lambda/aws"
  version = "8.13.0"

  function_name = "publisher"
  description   = "My awesome lambda publisher"
  handler       = "lambda-publisher.lambda_handler"
  runtime       = "python3.14"

  source_path = "./src/lambda-publisher.py"

  environment_variables = {
    QUEUE_URL = aws_sqs_queue.terraform_queue.url
  }

  attach_policy_json = true
  policy_name        = "write_to_sqs_policy"
  policy_json        = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": {
      "Effect": "Allow",
      "Action": "sqs:SendMessage",
      "Resource": "${aws_sqs_queue.terraform_queue.arn}"
  }
}
POLICY

  tags = {
    Name = "lambda-publisher"
  }
}
