module "lambda_receiver" {
  source = "terraform-aws-modules/lambda/aws"

  function_name = "lambda-receiver"
  handler       = "lambda-receiver.lambda_handler"
  runtime       = "python3.8"

  source_path = "./src/lambda-receiver.py"

  attach_policy_json = true
  policy_name = "receive_from_sqs_policy"
  policy_json = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": {
      "Effect": "Allow",
      "Action": "sqs:ReceiveMessage",
      "Resource": "${aws_sqs_queue.terraform_queue.arn}"
  }
}
POLICY

  tags = {
    Name = "lambda-receiver"
  }
}

resource "aws_lambda_event_source_mapping" "listener_lambda_mapping" {
  batch_size        = 10
  event_source_arn  = aws_sqs_queue.terraform_queue.arn
  enabled           = true
  function_name     = module.lambda_receiver.lambda_function_name
}