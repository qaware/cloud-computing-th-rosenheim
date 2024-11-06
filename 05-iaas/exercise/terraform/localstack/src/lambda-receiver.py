import boto3
import json

# Replace 'YOUR_BUCKET_NAME' with your S3 bucket name
BUCKET_NAME = 'result-bucket'

def lambda_handler(event, context):
    # Initialize the S3 client
    s3 = boto3.client('s3', endpoint_url = 'http://localstack:4566')

    try:
        # Convert the data to a JSON string
        data_json = json.dumps(event)

        # Define the S3 object key (file name) where you want to store the data
        object_key = "index.json"

        # Upload the data to the S3 bucket
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=object_key,
            Body=data_json
        )

        return {
            'statusCode': 200,
            'body': 'Data uploaded to S3 bucket successfully!'
        }
    except Exception as e:
        print(f"Error: {str(e)}")
        return {
            'statusCode': 500,
            'body': 'Error uploading data to S3 bucket.'
        }