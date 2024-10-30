resource "aws_s3_bucket" "result-bucket" {
  bucket = "result-bucket"
}

resource "aws_s3_bucket_website_configuration" "example" {
  bucket = aws_s3_bucket.result-bucket.id

  index_document {
    suffix = "index.json"
  }
}