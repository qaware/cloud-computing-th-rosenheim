module "lambda_receiver" {
  # https://github.com/terraform-aws-modules/terraform-aws-lambda/blob/master/CHANGELOG.md
  source  = "terraform-aws-modules/lambda/aws"
  version = "8.13.0"

  function_name = "receiver"
  description   = "My awesome lambda receiver"
  handler       = "lambda-receiver.lambda_handler"
  runtime       = "python3.14"

  source_path = "./src/lambda-receiver.py"

  environment_variables = {
    BUCKET_NAME = "result-bucket"
  }

  attach_policy_json = true
  policy_name        = "receive_from_sqs_policy"
  policy_json        = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "sqs:ReceiveMessage",
        "sqs:DeleteMessage",
        "sqs:GetQueueAttributes"
      ],
      "Resource": "${aws_sqs_queue.terraform_queue.arn}"
    },
    {
      "Effect": "Allow",
      "Action": "s3:PutObject",
      "Resource": "arn:aws:s3:::result-bucket/*"
    }
  ]
}
POLICY

  tags = {
    Name = "lambda-receiver"
  }
}

resource "aws_lambda_event_source_mapping" "listener_lambda_mapping" {
  batch_size       = 10
  event_source_arn = aws_sqs_queue.terraform_queue.arn
  enabled          = true
  function_name    = module.lambda_receiver.lambda_function_name
}
