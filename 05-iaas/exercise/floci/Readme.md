# Exercise: Infrastructure as Code with Terraform on AWS with Floci

[Floci](https://github.com/floci-io/floci) is free, open-source software that emulates the AWS Cloud locally for testing and development purposes.  
Conveniently, Terraform and its AWS modules work against Floci the same way as against AWS.  
The only difference is the [provider.tf](provider.tf), which sends all requests to Floci instead of the real AWS Cloud.
The configurations you create could, therefore, also be deployed to AWS by only changing the provider configuration.

## Prerequisites
We provide a fully functional Docker Compose setup so you can start right away.  
Open a terminal and run the following command:
```shell
$ docker compose up
```
The process may take a few minutes.  
You should now have a Floci container running.  
You will notice that one container immediately stopped.  
This container provides all the necessary tools for today's exercise and must be explicitly started interactively.
So open a second terminal and run the following command:
```shell
$ docker compose run --rm iaas-container
```
You are now in a shell in the container as the root user, in the `/root/terraform` directory.  
This directory is mounted from the host and contains the Terraform configuration files of this exercise.
The configurations are currently empty and need to be expanded as part of the exercise.

> __INFO__  
> Floci runs every Lambda function in its own Docker container.
> That is why the Floci container needs access to the Docker socket of your host.
> You will see these containers appear in `docker ps` once you invoke the functions.

## Target Architecture
In this exercise, we will develop the following infrastructure:

Lambda Publisher Function → SQS Queue → Lambda Receiver Function → S3 Bucket

In essence, we are building a Publish/Subscribe system with Lambda functions.  
All components are provisioned with Terraform.  
The Lambda publisher function writes a message to the SQS queue when executed.  
The Lambda receiver function subscribes to the SQS queue and, upon execution, writes the payload to an S3 bucket.  
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
Here is the documentation for the resource: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/sqs_queue

Terraform requires an initialization step where providers and modules are downloaded.  
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

Use the AWS CLI to verify that a queue has been created.  
The AWS CLI in the container is already configured to talk to Floci instead of AWS.
This is done with the following environment variables, which are set in the [docker-compose.yml](docker-compose.yml):
```shell
AWS_ENDPOINT_URL=http://floci:4566
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test
```

List the available SQS queues:
```shell
$ aws sqs list-queues
```
You should receive the following output:
```json
{
    "QueueUrls": [
        "http://floci:4566/000000000000/terraform-example-queue"
    ]
}
```

## Creating the Lambda Publisher
Now we want to create the Lambda function that writes to the SQS queue just created.  
Lambda functions are essentially AWS-hosted executable code.  
If you're interested, you can view the code in the [src](src) directory.  
Our goal is to provision this code with Terraform.

Use the following configuration:
```terraform
module "lambda_publisher" {
  source  = "terraform-aws-modules/lambda/aws"
  version = "8.13.0"

  function_name = "publisher"
  description   = "My awesome lambda publisher"
  handler       = "lambda-publisher.lambda_handler"
  runtime       = "python3.14"

  source_path = "./src/lambda-publisher.py"

  environment_variables = {
    QUEUE_URL = TODO
  }

  attach_policy_json = true
  policy_name        = "write_to_sqs_policy"
  policy_json        = <<POLICY
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

Replace the `TODO`s with the URL and the ARN of the SQS queue.  
Refer to the attributes of the SQS queue resource.  
As you added a new module, you need to run `terraform init` again before `terraform apply`.

> __INFO__  
> ARN - Amazon Resource Name  
> The ARN is a unique reference in AWS.

> __INFO__  
> For one AWS service to communicate with another AWS service, appropriate permissions must be granted.
> The policy allows the Lambda function to write messages to this one SQS queue.
> However, there is more happening in the Lambda module behind the scenes to make this possible.
> An introduction to the AWS IAM system would go beyond the scope of this exercise.

> __INFO__  
> The Lambda code does not contain any Floci-specific configuration.
> Floci passes the environment variable `AWS_ENDPOINT_URL` to the Lambda containers, which the AWS SDK picks up automatically.
> The same code would run unchanged in AWS.

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
$ aws sqs receive-message --queue-url http://floci:4566/000000000000/terraform-example-queue
```

## Creating the Lambda Receiver
Now we want to create the receiver.  
Use the following configuration:
```terraform
module "lambda_receiver" {
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
      "Resource": "${TODO}"
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
```

Once the receiver is created, nothing will happen initially.  
The Lambda function must first be "wired" to the SQS queue.  
Use the [aws_lambda_event_source_mapping](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lambda_event_source_mapping) resource for this.

If everything is provisioned correctly, the receiver will now read from the SQS queue, but the result will not be successfully written.  
Check the logs of the receiver to find out why:
```shell
$ aws logs tail /aws/lambda/receiver
```

## Creating an S3 Bucket
Create an S3 bucket configured for static website hosting.  
Use the resources [aws_s3_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) and [aws_s3_bucket_website_configuration](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_website_configuration).  
Use `index.json` as the index document, as this is the file the receiver writes.

__Important__: The bucket must be named `result-bucket`.  
S3 buckets are addressed by name in website hosting, even within AWS itself.
Bucket names must be globally unique, so you can also use the name as a domain.  
In our Floci setup, the name is critical because DNS resolution only works with this name.

If you created the bucket correctly, the following should work and provide a result:
```shell
$ aws lambda invoke --function-name publisher output.txt
$ curl http://result-bucket.s3-website-us-east-1.floci:4566/
```

## Cleanup
Destroy all created resources with:
```shell
$ terraform destroy
```

If the S3 bucket can't be deleted because it is not empty, have a look at the `force_destroy` argument of the bucket resource.

Leave the container with `exit` and stop Floci with `docker compose down` in the first terminal.  
Floci keeps all resources in memory, so stopping it discards everything that may be left.
