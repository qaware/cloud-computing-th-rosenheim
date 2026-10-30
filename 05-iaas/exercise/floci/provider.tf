terraform {
  required_version = ">= 1.17.0"

  required_providers {
    aws = {
      # https://github.com/hashicorp/terraform-provider-aws/blob/main/CHANGELOG.md
      source  = "hashicorp/aws"
      version = "6.71.0"
    }
  }
}

# Floci accepts any credentials, so we use dummy values and skip all checks against the real AWS Cloud.
provider "aws" {
  access_key                  = "test"
  secret_key                  = "test"
  region                      = "us-east-1"
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true
  s3_use_path_style           = true

  # Send the requests of every service we use to Floci instead of the real AWS Cloud.
  endpoints {
    iam    = "http://floci:4566"
    lambda = "http://floci:4566"
    logs   = "http://floci:4566"
    s3     = "http://floci:4566"
    sqs    = "http://floci:4566"
    sts    = "http://floci:4566"
  }
}
