import json
import os

import boto3

# The bucket name is passed in by Terraform as an environment variable
BUCKET_NAME = os.environ['BUCKET_NAME']

def lambda_handler(event, context):
    # Initialize the S3 client
    # No endpoint is configured here: in AWS the client uses the real endpoint,
    # in Floci the AWS_ENDPOINT_URL environment variable injected by Floci is used.
    s3 = boto3.client('s3')

    try:
        # Convert the data to a JSON string
        data_json = json.dumps(event)

        # Define the S3 object key (file name) where you want to store the data
        object_key = "index.json"

        # Upload the data to the S3 bucket
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=object_key,
            Body=data_json,
            ContentType='application/json'
        )

        return {
            'statusCode': 200,
            'body': 'Data uploaded to S3 bucket successfully!'
        }
    except Exception as e:
        print(f"Error: {str(e)}")
        # Raise the error, so the message is not deleted from the queue and will be retried
        raise
