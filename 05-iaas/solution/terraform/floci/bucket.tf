resource "aws_s3_bucket" "result_bucket" {
  bucket = "result-bucket"

  # Allows terraform destroy to delete the bucket including the objects written by the receiver
  force_destroy = true
}

resource "aws_s3_bucket_website_configuration" "result_bucket" {
  bucket = aws_s3_bucket.result_bucket.id

  index_document {
    suffix = "index.json"
  }
}
