terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region                      = "us-east-1"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    s3             = "http://localhost:4566"
    ec2            = "http://localhost:4566"
    iam            = "http://localhost:4566"
    lambda         = "http://localhost:4566"
    apigateway     = "http://localhost:4566"
    cloudformation = "http://localhost:4566"
    sts            = "http://localhost:4566"
    ssm            = "http://localhost:4566"
    secretsmanager = "http://localhost:4566"
    route53        = "http://localhost:4566"
    elbv2          = "http://localhost:4566"
    autoscaling    = "http://localhost:4566"
    cloudwatch     = "http://localhost:4566"
    logs           = "http://localhost:4566"
  }
  
  s3_use_path_style = true
}
