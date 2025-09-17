# Terraform Technical Interview Assessment

This directory contains hands-on Terraform exercises designed to assess candidates' practical Infrastructure as Code (IaC) skills using LocalStack for AWS service simulation.

## Prerequisites

- Docker installed on your machine
- Docker Compose installed
- Terraform >= 1.0 installed
- Basic understanding of AWS services and Terraform concepts
- 4GB+ of available RAM
- macOS, Linux, or Windows with WSL2

## What is LocalStack?

LocalStack is a fully functional local AWS cloud stack that runs in a single container on your laptop or CI environment. It provides a local testing environment for AWS services without requiring an actual AWS account.

## Setup Instructions

### 1. Navigate to the Terraform exercises directory
```bash
cd exercises/terraform
```

### 2. Run the setup script
```bash
./setup-terraform.sh
```

This will:
- Install and start LocalStack (AWS service simulation)
- Initialize Terraform configurations for all exercises
- Set up the testing environment
- Generate SSH keys if needed

### 3. Verify your setup
```bash
./verify-terraform.sh
```

## Assessment Exercises

The assessment includes 4 progressive exercises that test different aspects of Terraform and AWS:

### Exercise 1: S3 Bucket Management
**Location:** `exercises/1-s3-bucket/`
**Skills Tested:** Basic resource creation, versioning, security, lifecycle management
**Requirements:**
- Create an S3 bucket with a unique name
- Enable versioning
- Block all public access
- Add appropriate tags
- Create a lifecycle rule to transition objects to IA after 30 days

### Exercise 2: EC2 Instance Deployment
**Location:** `exercises/2-ec2-instance/`
**Skills Tested:** Compute resources, security groups, AMI selection, key pairs
**Requirements:**
- Create a t2.micro EC2 instance
- Use the latest Amazon Linux 2 AMI
- Create a security group allowing SSH (22) and HTTP (80)
- Create and configure a key pair
- Add appropriate tags

### Exercise 3: Lambda Function Creation
**Location:** `exercises/3-lambda-function/`
**Skills Tested:** Serverless computing, IAM roles, function deployment
**Requirements:**
- Create a Lambda function with Python 3.9 runtime
- Create an IAM role for the function
- Implement a simple "Hello from Terraform!" function
- Add appropriate tags

### Exercise 4: VPC and Networking
**Location:** `exercises/4-vpc-networking/`
**Skills Tested:** Advanced networking, VPC design, subnets, routing, NAT
**Requirements:**
- Create a VPC with 10.0.0.0/16 CIDR
- Create 2 public and 2 private subnets across different AZs
- Set up Internet Gateway and NAT Gateway
- Configure route tables for public and private subnets
- Create security groups for web servers

## How to Complete an Exercise

1. **Navigate to the exercise directory:**
   ```bash
   cd exercises/1-s3-bucket
   ```

2. **Read the requirements in `main.tf`:**
   - Each exercise has detailed requirements and hints
   - Implement your solution in the `main.tf` file

3. **Test your implementation:**
   ```bash
   # Validate your Terraform configuration
   terraform plan
   
   # Run the test cases
   terraform test
   
   # Apply your changes to LocalStack
   terraform apply
   ```

4. **Verify your solution:**
   - All test cases should pass
   - Resources should be created successfully in LocalStack
   - Check the solution in `../solutions/` if you get stuck

## Detailed Testing Steps

### Step 1: Initialize Terraform
```bash
# Initialize Terraform and download providers
terraform init

# If you need to upgrade providers
terraform init -upgrade
```

### Step 2: Validate Configuration
```bash
# Check syntax and configuration
terraform validate

# Format your code
terraform fmt

# Check for security issues (if terraform-security installed)
terraform security
```

### Step 3: Plan Your Changes
```bash
# See what will be created/modified
terraform plan

# Save plan to file for review
terraform plan -out=tfplan

# Review saved plan
terraform show tfplan
```

### Step 4: Run Test Cases
```bash
# Run all test cases
terraform test

# Run specific test file
terraform test -test-file=test-cases/1-s3-bucket-test.tftest.hcl

# Run with verbose output
terraform test -verbose
```

### Step 5: Apply Changes
```bash
# Interactive apply (recommended for learning)
terraform apply

# Auto-approve (for quick testing)
terraform apply -auto-approve

# Apply from saved plan
terraform apply tfplan
```

### Step 6: Verify Resources
```bash
# List S3 buckets
aws --endpoint-url=http://localhost:4566 s3 ls

# Check EC2 instances
aws --endpoint-url=http://localhost:4566 ec2 describe-instances --region us-east-1

# List Lambda functions
aws --endpoint-url=http://localhost:4566 lambda list-functions --region us-east-1

# Check IAM roles
aws --endpoint-url=http://localhost:4566 iam list-roles --region us-east-1
```

### Step 7: Clean Up
```bash
# Destroy all resources
terraform destroy

# Auto-approve destroy
terraform destroy -auto-approve

# Remove state files (if needed)
rm -rf .terraform/
rm terraform.tfstate*
```

## Testing with Solutions

If you want to test the provided solutions:

1. **Copy the solution:**
   ```bash
   # Copy solution file
   cp ../../solutions/1-s3-bucket-solution.tf main.tf
   
   # Copy provider configuration
   cp ../../infrastructure/provider.tf .
   ```

2. **Follow the testing steps above**

3. **Verify the solution works:**
   ```bash
   # All test cases should pass
   terraform test
   
   # Resources should be created successfully
   terraform apply
   ```

## Test Framework

Each exercise includes comprehensive test cases using Terraform's built-in testing framework:

- **Resource Creation Tests:** Verify that required resources are created
- **Configuration Tests:** Validate resource configurations match requirements
- **Security Tests:** Ensure security best practices are followed
- **Tagging Tests:** Verify proper resource tagging

## Available AWS Services in LocalStack

The following AWS services are available for testing:
- S3 (Simple Storage Service)
- EC2 (Elastic Compute Cloud)
- IAM (Identity and Access Management)
- Lambda
- API Gateway
- CloudFormation
- STS (Security Token Service)
- SSM (Systems Manager)
- Secrets Manager
- Route 53
- ELBv2 (Elastic Load Balancing)
- Auto Scaling
- CloudWatch
- CloudWatch Logs

## Troubleshooting

### LocalStack Issues
```bash
# Check LocalStack status
curl http://localhost:4566/_localstack/health

# View LocalStack logs
docker-compose logs localstack

# Restart LocalStack
docker-compose restart localstack

# Check if services are available
aws --endpoint-url=http://localhost:4566 s3 ls --region us-east-1
```

### Terraform Issues
```bash
# Reinitialize Terraform
terraform init

# Clean up state
rm -rf .terraform/
rm terraform.tfstate*

# Reinitialize
terraform init

# Fix provider version conflicts
terraform init -upgrade
```

### Test Case Issues
```bash
# Run tests with verbose output
terraform test -verbose

# Check test file syntax
terraform test -test-file=test-cases/1-s3-bucket-test.tftest.hcl

# Validate test configuration
terraform validate
```

### Common Issues and Solutions

1. **Port conflicts:** 
   - Ensure ports 4566 and 4510-4559 are available
   - Check if other services are using these ports

2. **Memory issues:** 
   - Ensure you have at least 4GB RAM available
   - Close other applications if needed

3. **Docker issues:** 
   - Ensure Docker is running and you have proper permissions
   - Try `docker system prune` to free up space

4. **Provider configuration errors:**
   - Make sure you have the provider.tf file in your exercise directory
   - Check that the AWS provider version is compatible

5. **S3 bucket creation fails:**
   - Ensure `s3_use_path_style = true` is set in provider configuration
   - Check that LocalStack is running and accessible

6. **Test cases fail:**
   - Verify your resource names match the test expectations
   - Check that all required resources are created
   - Ensure proper tagging is applied

7. **State file issues:**
   - If state becomes corrupted, remove state files and reinitialize
   - Use `terraform import` to import existing resources if needed

## Cleanup

To clean up your local environment:
```bash
# Stop and remove containers
docker-compose down

# Remove LocalStack data (optional)
sudo rm -rf /tmp/localstack
```

## Assessment Criteria

Candidates will be evaluated on:
- **Correctness:** Does the solution meet all requirements?
- **Best Practices:** Are AWS and Terraform best practices followed?
- **Security:** Are security configurations appropriate?
- **Code Quality:** Is the code clean, readable, and maintainable?
- **Testing:** Do all test cases pass?

## Time Expectations

- **Exercise 1 (S3):** 15-20 minutes
- **Exercise 2 (EC2):** 20-25 minutes  
- **Exercise 3 (Lambda):** 25-30 minutes
- **Exercise 4 (VPC):** 30-40 minutes

**Total Assessment Time:** 90-115 minutes

## Getting Help

- Check the `solutions/` directory for reference implementations
- Review Terraform documentation: https://registry.terraform.io/providers/hashicorp/aws/latest/docs
- LocalStack documentation: https://docs.localstack.cloud/
