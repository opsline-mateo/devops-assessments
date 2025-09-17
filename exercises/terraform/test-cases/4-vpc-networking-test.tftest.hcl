# Test case for VPC Networking Exercise
run "vpc_created" {
  command = plan

  assert {
    condition     = aws_vpc.test_vpc != null
    error_message = "VPC should be created"
  }
}

run "vpc_correct_cidr" {
  command = plan

  assert {
    condition     = aws_vpc.test_vpc.cidr_block == "10.0.0.0/16"
    error_message = "VPC should have CIDR block 10.0.0.0/16"
  }
}

run "public_subnets_created" {
  command = plan

  assert {
    condition     = length(aws_subnet.public_subnets) == 2
    error_message = "Should create 2 public subnets"
  }
}

run "private_subnets_created" {
  command = plan

  assert {
    condition     = length(aws_subnet.private_subnets) == 2
    error_message = "Should create 2 private subnets"
  }
}

run "internet_gateway_created" {
  command = plan

  assert {
    condition     = aws_internet_gateway.test_igw != null
    error_message = "Internet Gateway should be created"
  }
}

run "nat_gateway_created" {
  command = plan

  assert {
    condition     = aws_nat_gateway.test_nat != null
    error_message = "NAT Gateway should be created"
  }
}

run "public_route_table_created" {
  command = plan

  assert {
    condition     = aws_route_table.public_rt != null
    error_message = "Public route table should be created"
  }
}

run "private_route_table_created" {
  command = plan

  assert {
    condition     = aws_route_table.private_rt != null
    error_message = "Private route table should be created"
  }
}

run "security_group_created" {
  command = plan

  assert {
    condition     = aws_security_group.web_sg != null
    error_message = "Web security group should be created"
  }
}

run "vpc_has_tags" {
  command = plan

  assert {
    condition     = aws_vpc.test_vpc.tags["Name"] == "terraform-test-vpc"
    error_message = "VPC should have Name tag"
  }
}
