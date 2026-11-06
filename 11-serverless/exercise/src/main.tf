terraform {
  required_version = ">= 1.8.0"

  required_providers {
    aws = {
      # https://github.com/hashicorp/terraform-provider-aws/blob/main/CHANGELOG.md
      source  = "hashicorp/aws"
      version = "5.50.0"
    }
  }
}

provider "aws" {
  region = "eu-central-1"
}




