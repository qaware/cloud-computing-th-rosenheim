import boto3

# Replace 'YOUR_QUEUE_URL' with your actual SQS queue URL
QUEUE_URL = 'http://localstack:4566/000000000000/terraform-example-queue'

def lambda_handler(event, context):
    # Initialize the SQS client
    sqs = boto3.client('sqs', endpoint_url = 'http://localstack:4566')

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