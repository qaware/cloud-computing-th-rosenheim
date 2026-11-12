### Creates a DynamoDB table that stores the short links.
### Each item looks like: { code: "aB3xY9", url: "https://...", clicks: 4, createdAt: "2026-11-12T..." }
resource "aws_dynamodb_table" "links" {
  name = "${var.name}-links"

  # On-demand billing: no capacity planning, you only pay per request.
  # An idle table costs (almost) nothing - just like an idle lambda function.
  billing_mode = "PAY_PER_REQUEST"

  # The partition key. DynamoDB uses it to distribute and look up items.
  # Only key attributes have to be declared here - all other attributes (url, clicks, ...) are schemaless.
  hash_key = "code"

  attribute {
    name = "code"
    type = "S" # S = string, N = number, B = binary
  }
}
