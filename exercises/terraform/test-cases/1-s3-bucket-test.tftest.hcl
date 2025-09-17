# Test case for S3 Bucket Exercise
run "s3_bucket_created" {
  command = plan

  assert {
    condition     = aws_s3_bucket.test_bucket != null
    error_message = "S3 bucket should be created"
  }
}

run "s3_bucket_versioning_enabled" {
  command = plan

  assert {
    condition     = aws_s3_bucket_versioning.test_bucket.versioning_configuration[0].status == "Enabled"
    error_message = "S3 bucket versioning should be enabled"
  }
}

run "s3_bucket_public_access_blocked" {
  command = plan

  assert {
    condition     = aws_s3_bucket_public_access_block.test_bucket.block_public_acls == true
    error_message = "S3 bucket should block public access"
  }

  assert {
    condition     = aws_s3_bucket_public_access_block.test_bucket.block_public_policy == true
    error_message = "S3 bucket should block public policy"
  }
}

run "s3_bucket_has_lifecycle_rule" {
  command = plan

  assert {
    condition     = length(aws_s3_bucket_lifecycle_configuration.test_bucket.rule) > 0
    error_message = "S3 bucket should have lifecycle rules"
  }
}

run "s3_bucket_has_tags" {
  command = plan

  assert {
    condition     = aws_s3_bucket.test_bucket.tags["Environment"] == "test"
    error_message = "S3 bucket should have Environment tag set to test"
  }
}
