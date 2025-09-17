# Test case for EC2 Instance Exercise
run "ec2_instance_created" {
  command = plan

  assert {
    condition     = aws_instance.test_instance != null
    error_message = "EC2 instance should be created"
  }
}

run "ec2_instance_correct_type" {
  command = plan

  assert {
    condition     = aws_instance.test_instance.instance_type == "t2.micro"
    error_message = "EC2 instance should be t2.micro"
  }
}

run "security_group_created" {
  command = plan

  assert {
    condition     = aws_security_group.test_sg != null
    error_message = "Security group should be created"
  }
}

run "security_group_ssh_rule" {
  command = plan

  assert {
    condition     = length([for rule in aws_security_group.test_sg.ingress : rule if rule.from_port == 22 && rule.to_port == 22]) > 0
    error_message = "Security group should allow SSH on port 22"
  }
}

run "security_group_http_rule" {
  command = plan

  assert {
    condition     = length([for rule in aws_security_group.test_sg.ingress : rule if rule.from_port == 80 && rule.to_port == 80]) > 0
    error_message = "Security group should allow HTTP on port 80"
  }
}

run "key_pair_created" {
  command = plan

  assert {
    condition     = aws_key_pair.test_key != null
    error_message = "Key pair should be created"
  }
}

run "instance_has_tags" {
  command = plan

  assert {
    condition     = aws_instance.test_instance.tags["Name"] == "terraform-test-instance"
    error_message = "EC2 instance should have Name tag"
  }
}
