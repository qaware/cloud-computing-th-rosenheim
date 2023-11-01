module "lambda_publisher" {
  source = "terraform-aws-modules/lambda/aws"

  function_name = "publisher"
  description   = "My awesome lambda publisher"
  handler       = "lambda-publisher.lambda_handler"
  runtime       = "python3.8"

  source_path = "./src/lambda-publisher.py"

  attach_policy_json = true
  policy_name = "write_to_sqs_policy"
  policy_json = <<POLICY
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

