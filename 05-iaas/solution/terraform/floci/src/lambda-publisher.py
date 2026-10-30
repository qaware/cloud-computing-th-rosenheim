import os

import boto3

# The queue URL is passed in by Terraform as an environment variable
QUEUE_URL = os.environ['QUEUE_URL']

def lambda_handler(event, context):
    # Initialize the SQS client
    # No endpoint is configured here: in AWS the client uses the real endpoint,
    # in Floci the AWS_ENDPOINT_URL environment variable injected by Floci is used.
    sqs = boto3.client('sqs')

    # Define the message to send
    message = "Hello, World!"

    try:
        # Send the message to the SQS queue
        response = sqs.send_message(
            QueueUrl=QUEUE_URL,
            MessageBody=message
        )

        # Print the response for debugging
        print(f"Message sent with MessageId: {response['MessageId']}")

        return {
            'statusCode': 200,
            'body': 'Message sent successfully!'
        }
    except Exception as e:
        print(f"Error: {str(e)}")
        return {
            'statusCode': 500,
            'body': str(e)
        }
