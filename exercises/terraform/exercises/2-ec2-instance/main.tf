# Exercise 2: Create an EC2 Instance
# 
# TASK: Create an EC2 instance with the following requirements:
# - Use t2.micro instance type
# - Use the latest Amazon Linux 2 AMI
# - Create a security group that allows SSH (port 22) and HTTP (port 80)
# - Add appropriate tags


# TODO: Implement the EC2 instance and related resources
# HINT: You'll need aws_instance, aws_security_group

# Your solution here:


# Solution for Exercise 2: EC2 Instance

# Data source for latest Amazon Linux 2 AMI
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

# Key pair
resource "aws_key_pair" "test_key" {
  key_name   = "terraform-test-key"
  public_key = file("~/.ssh/id_rsa.pub") # Assumes SSH key exists
}


# VPC (minimal for this exercise)
resource "aws_vpc" "test_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "terraform-test-vpc"
  }
}

# Public subnet
resource "aws_subnets" "public_subnets" {
  count             = 2
  vpc_id            = aws_vpc.test_vpc.id
  cidr_block        = "10.0.0.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]

  map_public_ip_on_launch = true

  tags = {
    Name = "terraform-test-public-subnet"
  }
}

# Data source for availability zones
data "aws_availability_zones" "available" {
  state = "available"
}
