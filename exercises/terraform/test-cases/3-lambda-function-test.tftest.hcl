# Test case for Lambda Function Exercise
run "lambda_function_created" {
  command = plan

  assert {
    condition     = aws_lambda_function.test_function != null
    error_message = "Lambda function should be created"
  }
}

run "lambda_function_correct_name" {
  command = plan

  assert {
    condition     = aws_lambda_function.test_function.function_name == "terraform-test-function"
    error_message = "Lambda function should have correct name"
  }
}

run "lambda_function_correct_runtime" {
  command = plan

  assert {
    condition     = aws_lambda_function.test_function.runtime == "python3.9"
    error_message = "Lambda function should use Python 3.9 runtime"
  }
}

run "iam_role_created" {
  command = plan

  assert {
    condition     = aws_iam_role.lambda_role != null
    error_message = "IAM role should be created for Lambda function"
  }
}

run "lambda_execution_policy_attached" {
  command = plan

  assert {
    condition     = aws_iam_role_policy_attachment.lambda_execution != null
    error_message = "Lambda execution policy should be attached"
  }
}

run "lambda_function_has_tags" {
  command = plan

  assert {
    condition     = aws_lambda_function.test_function.tags["Environment"] == "test"
    error_message = "Lambda function should have Environment tag"
  }
}
