# Java CDK Technical Interview Assessment

This directory contains hands-on Java CDK exercises designed to assess candidates' practical Infrastructure as Code (IaC) skills using AWS CDK for Java.

## Prerequisites

- Java 11 or higher installed
- Maven 3.6+ installed
- AWS CLI configured (or LocalStack for local testing)
- Basic understanding of AWS services and CDK concepts
- 4GB+ of available RAM
- macOS, Linux, or Windows with WSL2

## What is AWS CDK?

AWS Cloud Development Kit (CDK) is an open-source software development framework to define cloud infrastructure in code and provision it through AWS CloudFormation. The CDK supports multiple programming languages including Java.

## Setup Instructions

### 1. Navigate to the Java CDK exercises directory
```bash
cd exercises/java-cdk
```

### 2. Run the setup script
```bash
./setup-java-cdk.sh
```

This will:
- Install AWS CDK CLI globally
- Initialize CDK projects for all exercises
- Set up the testing environment
- Configure Maven dependencies

### 3. Verify your setup
```bash
./verify-java-cdk.sh
```

## Assessment Exercises

The assessment includes 4 progressive exercises that test different aspects of Java CDK and AWS:

### Exercise 1: Lambda Function Creation
**Location:** `exercises/1-lambda-function/`
**Skills Tested:** Basic CDK constructs, Lambda functions, IAM roles, environment variables
**Requirements:**
- Create a Lambda function with Java runtime
- Configure appropriate IAM permissions
- Set environment variables
- Add proper logging configuration
- Create a dead letter queue for failed executions

### Exercise 2: S3 Bucket with Event Notifications
**Location:** `exercises/2-s3-bucket/`
**Skills Tested:** S3 constructs, event notifications, Lambda triggers, bucket policies
**Requirements:**
- Create an S3 bucket with versioning enabled
- Configure event notifications to trigger Lambda
- Set up proper bucket policies
- Add lifecycle rules for object transitions
- Implement cross-origin resource sharing (CORS)

### Exercise 3: API Gateway with Lambda Integration
**Location:** `exercises/3-api-gateway/`
**Skills Tested:** API Gateway constructs, Lambda integration, CORS, authentication
**Requirements:**
- Create a REST API with multiple endpoints
- Integrate with Lambda functions
- Configure CORS for web applications
- Add API key authentication
- Set up request/response validation

### Exercise 4: DynamoDB Table with Global Secondary Indexes
**Location:** `exercises/4-dynamodb-table/`
**Skills Tested:** DynamoDB constructs, GSI, auto-scaling, backup policies
**Requirements:**
- Create a DynamoDB table with proper key schema
- Add Global Secondary Indexes (GSI)
- Configure auto-scaling for read/write capacity
- Set up point-in-time recovery
- Add backup and restore policies

## Working with Exercises

### Step 1: Navigate to an Exercise
```bash
cd exercises/1-lambda-function
```

### Step 2: Install Dependencies
```bash
mvn clean install
```

### Step 3: Synthesize CDK Template
```bash
cdk synth
```

### Step 4: Run Test Cases
```bash
# Run all test cases
mvn test

# Run specific test class
mvn test -Dtest=LambdaFunctionTest

# Run with verbose output
mvn test -Dtest=LambdaFunctionTest -Dmaven.test.failure.ignore=true
```

### Step 5: Deploy Changes
```bash
# Deploy to AWS (requires AWS credentials)
cdk deploy

# Deploy with specific context
cdk deploy --context environment=dev

# Deploy with approval
cdk deploy --require-approval never
```

### Step 6: Verify Resources
```bash
# List Lambda functions
aws lambda list-functions

# Check S3 buckets
aws s3 ls

# List API Gateway APIs
aws apigateway get-rest-apis

# Check DynamoDB tables
aws dynamodb list-tables
```

### Step 7: Clean Up
```bash
# Destroy all resources
cdk destroy

# Destroy specific stack
cdk destroy MyStack

# Force destroy without confirmation
cdk destroy --force
```

## Testing with Solutions

If you want to test the provided solutions:

1. **Copy the solution:**
   ```bash
   # Copy solution file
   cp ../../solutions/1-lambda-function-solution.java src/main/java/com/myorg/
   
   # Copy test files
   cp ../../test-cases/1-lambda-function-test.java src/test/java/
   ```

2. **Follow the testing steps above**

3. **Verify the solution works:**
   ```bash
   # All test cases should pass
   mvn test
   
   # Resources should be created successfully
   cdk deploy
   ```

## Test Framework

Each exercise includes comprehensive test cases using JUnit 5 and CDK assertions:

- **Resource Creation Tests:** Verify that required resources are created
- **Configuration Tests:** Validate resource configurations match requirements
- **Security Tests:** Ensure security best practices are followed
- **Integration Tests:** Verify proper resource relationships and dependencies

## Common Issues and Troubleshooting

1. **CDK not found:**
   - Ensure CDK is installed globally: `npm install -g aws-cdk`
   - Check PATH includes CDK binary location

2. **Maven build fails:**
   - Verify Java version compatibility
   - Check Maven settings and repository configuration
   - Ensure all dependencies are available

3. **AWS credentials not found:**
   - Configure AWS CLI: `aws configure`
   - Set environment variables: `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`
   - Use AWS profiles: `--profile myprofile`

4. **CDK bootstrap required:**
   - Run `cdk bootstrap` in your AWS account
   - This creates the CDK toolkit stack

5. **Test cases fail:**
   - Verify your resource names match the test expectations
   - Check that all required resources are created
   - Ensure proper configurations are applied

6. **Deployment fails:**
   - Check CloudFormation events in AWS Console
   - Verify IAM permissions for CDK deployment
   - Review CDK context and environment variables

## Cleanup

To clean up your AWS environment:
```bash
# Destroy all CDK stacks
cdk destroy --all

# Remove CDK bootstrap stack (if no longer needed)
aws cloudformation delete-stack --stack-name CDKToolkit
```

## Assessment Criteria

Candidates will be evaluated on:
- **Correctness:** Does the solution meet all requirements?
- **Best Practices:** Are AWS and CDK best practices followed?
- **Security:** Are security configurations appropriate?
- **Code Quality:** Is the code clean, readable, and maintainable?
- **Testing:** Do all test cases pass?
- **Bug Detection:** Can the candidate identify and fix intentional bugs?

## Time Expectations

- **Exercise 1 (Lambda):** 20-25 minutes
- **Exercise 2 (S3):** 25-30 minutes  
- **Exercise 3 (API Gateway):** 30-35 minutes
- **Exercise 4 (DynamoDB):** 35-40 minutes

**Total Assessment Time:** 110-130 minutes

## Getting Help

- Check the `solutions/` directory for reference implementations
- Review AWS CDK documentation: https://docs.aws.amazon.com/cdk/v2/guide/home.html
- Java CDK API reference: https://docs.aws.amazon.com/cdk/api/v2/java/
