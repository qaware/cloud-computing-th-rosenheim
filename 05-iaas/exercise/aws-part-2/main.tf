terraform {
  required_version = ">= 1.8.0"

  required_providers {
    aws = {
      # https://github.com/hashicorp/terraform-provider-aws/blob/main/CHANGELOG.md
      source  = "hashicorp/aws"
      version = "5.50.0"
    }
    random = {
      # https://github.com/hashicorp/terraform-provider-random/blob/main/CHANGELOG.md
      source  = "hashicorp/random"
      version = "3.6.2"
    }
  }
}

################################################################################

provider "aws" {
  region = "eu-central-1"
}

################################################################################

resource "random_string" "id_suffix" {
  length  = 4
  special = false
  upper   = false
}

locals {
  # Use this variable as prefix for all resource names.
  # This avoids conflicts with globally unique resources (all resources with a hostname).
  env = "default"

  # Use this map to apply env-specific values for certain components.
  env_config = {
    default = {
      message = "Hello World!"
    }
    dev = {
      message = "Hello DEV Workspaces!"
    }
    test = {
      message = "Hello TEST Workspaces!"
    }
    prod = {
      message = "Hello PROD Workspaces!"
    }
  }
  config = merge(local.env_config["default"], lookup(local.env_config, terraform.workspace, {}))

  # Tag all resources at least with these tags.
  # Allows filtering in AWS and distinction between environments.
  standard_tags = {
    "environment" = local.env
  }

  standard_tags_asg = [
    for key in keys(local.standard_tags) : {
      key                 = key
      value               = lookup(local.standard_tags, key)
      propagate_at_launch = true
    }
  ]
}
