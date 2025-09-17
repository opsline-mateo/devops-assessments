# TODO: 
#   Random string for unique bucket name
#   S3 bucket resource with proper naming
#   Appropriate tags
#   Versioning enabled
#   Public access blocked
# ✅ Lifecycle rule for transitioning to IA after 30 days


# Lifecycle rule
resource "aws_s3_bucket_lifecycle_configuration" "test_bucket" {
  rule {
    id     = "transition_to_ia"
    status = "Enabled"

    filter {
      prefix = ""
    }

    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }
}
