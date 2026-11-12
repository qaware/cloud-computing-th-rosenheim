### IAM roles for the lambda functions. A lambda function assumes its role to get permissions for other AWS services.
###
### - Course account: you are not allowed to create IAM roles. Your instructor prepared shared roles, which are looked up here.
### - Own account: the roles are created by terraform.
###
### `count` is terraform's way of creating a resource conditionally: count = 0 means "don't create it".

### ---- Course account: look up the existing roles ----

data "aws_iam_role" "hello" {
  count = var.course_account ? 1 : 0
  name  = "BasicLambdaExecutionRole"
}

data "aws_iam_role" "links" {
  count = var.course_account ? 1 : 0
  name  = "DynamoLambdaExecutionRole"
}

### ---- Own account: create the roles ----

locals {
  # Allows the lambda service to assume the roles
  lambda_assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "lambda.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role" "hello" {
  count              = var.course_account ? 0 : 1
  name               = "${var.name}-hello-role"
  assume_role_policy = local.lambda_assume_role_policy
}

# AWS managed policy that allows writing logs to cloudwatch
resource "aws_iam_role_policy_attachment" "hello_logs" {
  count      = var.course_account ? 0 : 1
  role       = aws_iam_role.hello[0].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role" "links" {
  count              = var.course_account ? 0 : 1
  name               = "${var.name}-links-role"
  assume_role_policy = local.lambda_assume_role_policy
}

resource "aws_iam_role_policy_attachment" "links_logs" {
  count      = var.course_account ? 0 : 1
  role       = aws_iam_role.links[0].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# Least privilege: only the item operations the function needs, only on our table
resource "aws_iam_role_policy" "links_dynamodb" {
  count = var.course_account ? 0 : 1
  name  = "${var.name}-links-dynamodb"
  role  = aws_iam_role.links[0].name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["dynamodb:GetItem", "dynamodb:PutItem", "dynamodb:UpdateItem"]
      Resource = aws_dynamodb_table.links.arn
    }]
  })
}

### ---- The role ARNs used by the lambda functions, regardless of the account type ----

locals {
  hello_role_arn = var.course_account ? one(data.aws_iam_role.hello[*].arn) : one(aws_iam_role.hello[*].arn)
  links_role_arn = var.course_account ? one(data.aws_iam_role.links[*].arn) : one(aws_iam_role.links[*].arn)
}
