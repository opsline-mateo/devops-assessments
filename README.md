# Technical Interview Assessment

This repository contains hands-on exercises designed to assess candidates' practical skills in Kubernetes, Terraform, and Java CDK.

## Available Assessments

### 🚀 Kubernetes Assessment
Hands-on Kubernetes exercises using kind (Kubernetes in Docker) to test practical k8s skills.

### ☁️ Terraform Assessment  
Infrastructure as Code exercises using LocalStack to simulate AWS services without requiring an AWS account.

### ☕ Java CDK Assessment
AWS Cloud Development Kit exercises using Java to test practical CDK and AWS skills with intentional bugs for candidates to identify and fix.

## Prerequisites

- Docker installed on your machine
- Docker Compose installed
- Java 11+ installed (for Java CDK assessment)
- Maven 3.6+ installed (for Java CDK assessment)
- Node.js 14+ installed (for Java CDK assessment)
- Basic understanding of Kubernetes, Terraform, and/or Java CDK concepts
- 4GB+ of available RAM
- macOS, Linux, or Windows with WSL2

## Quick Start

### Kubernetes Assessment
```bash
# Clone and setup
git clone https://github.com/opsline-mateo/k8s-assessment.git
cd k8s-assessment

# Setup Kubernetes environment
cd setup
./setup-cluster.sh

# Verify setup
./verify-setup.sh

# Start with exercises
cd ../exercises/k8s
```

### Terraform Assessment
```bash
# Clone and setup
git clone https://github.com/opsline-mateo/devops-assessment.git
cd real-devops-assessment

# Setup Terraform environment
cd exercises/terraform
./setup-terraform.sh

# Verify setup
./verify-terraform.sh

# Start with exercises
cd exercises/1-s3-bucket
```

### Java CDK Assessment
```bash
# Clone and setup
git clone https://github.com/opsline-mateo/k8s-assessment.git
cd real-devops-assessment

# Setup Java CDK environment
cd exercises/java-cdk
./setup-java-cdk.sh

# Verify setup
./verify-java-cdk.sh

# Start with exercises
cd exercises/1-s3-bucket

## Assessment Exercises

### Kubernetes Exercises
Located in `exercises/k8s/`, these exercises test:
- Pod troubleshooting and debugging
- Service and Ingress configuration
- ConfigMap and Secret management
- Application deployment and scaling

### Terraform Exercises
Located in `exercises/terraform/exercises/`, these exercises test:
- S3 bucket management and security
- EC2 instance deployment and networking
### Java CDK Exercises
Located in `exercises/java-cdk/exercises/`, these exercises test:
- S3 bucket with event notifications and CORS
- WAF rule

Each exercise includes:
- Problem statement and requirements
- Expected deliverables
- Hints and guidance
- Automated test cases
- Reference solutions
- **Intentional bugs for candidates to identify and fix**

## Cleanup

### Kubernetes Environment
```bash
cd setup
./cleanup.sh
```

### Terraform Environment
```bash
cd exercises/terraform
docker-compose down
```

### Java CDK Environment
```bash
cd exercises/java-cdk
# Destroy all CDK stacks
cdk destroy --all

# Remove CDK bootstrap stack (if no longer needed)
aws cloudformation delete-stack --stack-name CDKToolkit
```
