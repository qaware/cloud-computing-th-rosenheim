# Exercise: Infrastructure as Code with Terraform on AWS with LocalStack

[LocalStack](https://localstack.cloud/) is software that emulates the AWS Cloud locally for testing and development purposes.  
Conveniently, AWS Terraform modules can also be applied unchanged on LocalStack.  
The configurations you create could, therefore, also be deployed and tested directly in AWS without modifications.

## Prerequisites
We provide a fully functional Docker Compose setup so you can start right away.  
Open a terminal and run the following commands:
```shell
$ docker compose up 
```
The process may take a few minutes.  
You should now have a LocalStack container running.  
You will notice that one container immediately stopped.  
This container provides all the necessary tools for today's exercise and must be explicitly started interactively.
So open a second terminal and run the following command:
```shell
$ docker compose run iaas-container 
```
You are now in a shell in the container as the root user.  
In the container, navigate to the `terraform/` directory.
This directory contains a volume mounted from the host with Terraform configuration files.
The configurations are currently empty and need to be expanded as part of the exercise.

## Target Architecture
In this exercise, we will develop the following infrastructure:

Lambda Publisher Function → SQS Queue → Lambda Subscriber Function → S3 Bucket

In essence, we are building a Publish/Subscribe system with Lambda functions.  
All components are provisioned with Terraform.  
The Lambda publisher function writes a message to the SQS queue when executed.  
The Lambda subscriber function subscribes to the SQS queue and, upon execution, writes the payload to an S3 bucket.  
The S3 bucket is configured for static website hosting, and you can use `curl` to view the result in the S3 bucket.

You don’t need an in-depth understanding of Lambda functions for this exercise.  
The functions themselves are provided for you.

## Creating the SQS Queue
First, we want to create an SQS queue.  
Create a file with a `.tf` extension and add the appropriate resource:
```terraform
resource "aws_sqs_queue" "terraform_queue" {
  # TODO
}
```
Configure the queue to be named `terraform-example-queue`.  
Here is the documentation for the module: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/sqs_queue

Terraform requires an initialization step where resources and modules are downloaded.  
In the container, run the following command:
```shell
$ terraform init
```

Now we want to check which resources Terraform will create for the given configuration:
```shell
$ terraform plan
```

If you are satisfied with the result, create the resources:
```shell
$ terraform apply
```

Use the AWS CLI to verify that a queue has been created: .
To make the AWS CLI work with the local setup, the following environment variables need to be set:
```shell
export AWS_ENDPOINT_URL=http://localstack:4566
export AWS_REGION=us-east-1
export AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
export AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

List the available SQS queues:
```shell
$ aws sqs list-queues
```
You should receive the following output:
```json
{
    "QueueUrls": [
        "http://localstack:4566/000000000000/terraform-example-queue"
    ]
}
```

## Creating the Lambda Publisher
Now we want to create the Lambda function that writes to the SQS queue just created.  
Lambda functions are essentially AWS-hosted executable code.  
If you're interested, you can view the code in the [src](../terraform/localstack/src) directory.  
Our goal is to provision this code with Terraform.

Use the following configuration:
```terraform
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
      "Resource": "${TODO}" 
  }
}
POLICY

  tags = {
    Name = "lambda-publisher"
  }
}
```

Replace `TODO` with the ARN of the SQS queue.  
Refer to the SQS queue resource.

> __INFO__  
> ARN - Amazon Resource Name  
> The ARN is a unique reference in AWS.

> __INFO__  
> For one AWS service to communicate with another AWS service, appropriate permissions must be granted.
> The policy allows the Lambda function to write messages to this one SQS queue.
> However, there is more happening in the Lambda module behind the scenes to make this possible.
> An introduction to the AWS IAM system would go beyond the scope of this exercise.

Execute the created Lambda function:
```shell
$ aws lambda invoke --function-name publisher output.txt
```
You should receive the following output in the terminal:
```json
{
    "StatusCode": 200,
    "ExecutedVersion": "$LATEST"
}
```
Also, check the contents of the `output.txt` file.

If everything worked, you can now retrieve the message from the SQS queue with the following command:
```shell
aws sqs receive-message --queue-url http://localhost:4566/000000000000/terraform-example-queue
```

## Creating the Lambda Subscriber
Now we want to create the subscriber.  
Use the following configuration:
```terraform
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
      "Resource": "${TODO}"
  }
}
POLICY

  tags = {
    Name = "lambda-receiver"
  }
}
```

Once the subscriber is created, nothing will happen initially.  
The Lambda function must first be "wired" to the SQS queue.  
Use the [aws_lambda_event_source_mapping](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lambda_event_source_mapping) resource for this.

If everything is provisioned correctly, the subscriber will now read from the SQS queue, but the result will not be successfully written.

## Creating an S3 Bucket
Create an S3 bucket configured for static website hosting.  
Use the resources [aws_s3_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) and [aws_s3_bucket_website_configuration](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_website_configuration).

__Important__: The bucket must be named `result-bucket`.  
S3 buckets are addressed by name in website hosting, even within AWS itself.
Bucket names must be globally unique, so you can also use the name as a domain.  
In our LocalStack setup, the name is critical because DNS resolution only works with this name.

If you created the bucket correctly, the following should work and provide a result:
```shell
$ aws lambda invoke --function-name publisher output.txt
$ curl result-bucket.s3.localhost.localstack.cloud:4566/index.json
```

## Cleanup
Destroy all created resources with:
```shell
$ terraform destroy
```