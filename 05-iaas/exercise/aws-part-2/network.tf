module "vpc" {
  # https://github.com/terraform-aws-modules/terraform-aws-vpc/tags
  source = "git::https://github.com/terraform-aws-modules/terraform-aws-vpc.git?ref=b3abd6df2ecf052451a361ed55b8f06f8742a795" # 6.7.3

  name           = local.env
  cidr           = "10.0.0.0/16"
  azs            = ["eu-central-1a", "eu-central-1b"]
  public_subnets = ["10.0.101.0/24", "10.0.102.0/24"]

  tags = local.standard_tags
}
