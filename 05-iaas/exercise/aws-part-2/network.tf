module "vpc" {
  # https://github.com/terraform-aws-modules/terraform-aws-vpc/tags
  source  = "git::https://github.com/terraform-aws-modules/terraform-aws-vpc.git?ref=25322b6b6be69db6cca7f167d7b0e5327156a595" # 5.8.1

  name           = local.env
  cidr           = "10.0.0.0/16"
  azs            = ["eu-central-1a", "eu-central-1b"]
  public_subnets = ["10.0.101.0/24", "10.0.102.0/24"]

  tags = local.standard_tags
}
