terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    http = {
      source  = "hashicorp/http"
      version = "~> 3.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "http" "my_ip" {
  url = "https://checkip.amazonaws.com"
}

# ── Latest Ubuntu 22.04 LTS AMI ─────
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# ── VPC ──
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name    = "${var.project_name}-vpc"
    Project = var.project_name
  }
}

# ── Internet Gateway ───
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name    = "${var.project_name}-igw"
    Project = var.project_name
  }
}

# ── Public Subnet ──────
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "af-south-1c"
  map_public_ip_on_launch = true

  tags = {
    Name    = "${var.project_name}-public-subnet"
    Project = var.project_name
  }
}

# ── Route Table ───
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name    = "${var.project_name}-rt"
    Project = var.project_name
  }
}

# ── Route Table Association ────────
resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# ── Security Group ─────────
resource "aws_security_group" "jenkins" {
  name        = "${var.project_name}-sg"
  description = "Security group for Jenkins server"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["${chomp(data.http.my_ip.response_body)}/32"]
  }

  ingress {
    description = "Jenkins UI"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Jenkins Agents"
    from_port   = 50000
    to_port     = 50000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name    = "${var.project_name}-sg"
    Project = var.project_name
  }
}

# ── Key Pair ─────
resource "aws_key_pair" "jenkins" {
  key_name   = "${var.project_name}-key"
  public_key = file(pathexpand(var.public_key_path))
}

# ── EC2 Instance ───────
resource "aws_instance" "jenkins" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.instance_type
  key_name                    = aws_key_pair.jenkins.key_name
  vpc_security_group_ids      = [aws_security_group.jenkins.id]
  subnet_id                   = aws_subnet.public.id
  associate_public_ip_address = true
  iam_instance_profile = aws_iam_instance_profile.jenkins_profile.name

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }

  user_data = file("${path.module}/user_data.sh")

  tags = {
    Name    = var.project_name
    Project = var.project_name
  }
}

# ── Elastic IP ─────
resource "aws_eip" "jenkins" {
  instance = aws_instance.jenkins.id
  domain   = "vpc"

  tags = {
    Name    = "${var.project_name}-eip"
    Project = var.project_name
  }
}

# ECR Repository for DevTracker
resource "aws_ecr_repository" "devtracker" {
  name = "devtracker"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name    = "devtracker"
    Project = var.project_name
  }
}

# IAM Role for EC2 to access ECR
resource "aws_iam_role" "jenkins_role" {
  name = "${var.project_name}-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

# IAM Policy to allow ECR access
resource "aws_iam_role_policy_attachment" "ecr_policy" {
  role       = aws_iam_role.jenkins_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess"
}

# IAM Instance Profile to attach to EC2 instance
resource "aws_iam_instance_profile" "jenkins_profile" {
  name = "${var.project_name}-profile"
  role = aws_iam_role.jenkins_role.name
}

# Application Server Instance

resource "aws_security_group" "app" {
  name        = "${var.project_name}-app-sg"
  description = "Security group for App server"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "App UI"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name    = "${var.project_name}-app-sg"
    Project = var.project_name
  }
}

resource "aws_instance" "app" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = "t3.small"
  key_name                    = aws_key_pair.jenkins.key_name
  vpc_security_group_ids = [aws_security_group.app.id]
  subnet_id                   = aws_subnet.public.id
  associate_public_ip_address = true
  iam_instance_profile        = aws_iam_instance_profile.jenkins_profile.name

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }

  user_data = <<-EOF
    #!/bin/bash
    apt-get update -y
    apt-get install -y ca-certificates curl gnupg awscli

    # Install Docker
    install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
      gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    chmod a+r /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
      tee /etc/apt/sources.list.d/docker.list > /dev/null
    apt-get update -y
    apt-get install -y docker-ce docker-ce-cli containerd.io

    systemctl enable docker
    systemctl start docker
    usermod -aG docker ubuntu

    # Add Jenkins public key for deployment
    mkdir -p /home/ubuntu/.ssh
    echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCqRxOqm3Rtk/klZUDqplHG5vxMajEzZ34wJJxbcE6GbahVxj+LZ6JmQgFpdcmNlSbwzuuiyxlulkFdkKW47i24SDtFkaOf0LpCbcgOWWK8Ky/pE93bzS0yQMa5ZawoPh0H0bQhs8qooxkJbLwuLCpIBFJ/5jg57KqiZWyN+FKvbThARAja8T7UojGDZd4PwGAa7qluc+fB9w5eb5/31At1R+LSIflS5ynWcYlpg3GaiU4nEAKbqeKmEaRmGHWs86eM0T2XxWVXTD/dzUVK9b5DEzFE+Y866VgmlwTwgtrmGrU8zIHLqWZoGgVbNH7KaTZI3J+N1mN+a8+C5qSOZVqqZZVzAac9BlOD7XpskxAPlP8+sW7mZhLAPwO+RYlN4fn3O1dYsWtUhBEsE8ShMDvVBjVQdqOzYP4eUT+dEx7F7GmhI9FnNRnTRw3oplgpELhecdpxvSucY3sJPfAwJXPTQWLx0jHTIefNsxRSlV4Fhkys9DDp99f435aWLAokwMNC9HJKmWiQInN8Q92MxaqiQf7Mv3XPZXr3Lh9gf9BwSZMcHn6E5i7a+Me9+OZWShCL9M8sUrcu7H9T6LlOjzWRgQAsUmjrJg0EMEBC0vDYzEHmYm2BBt1jozo65zVPj7xzrZFS3R6X0QR6vrRvWuxv9Knt+zcu+5Sdx2hdV/Fzbw== jenkins@ip-10-0-1-12" >> /home/ubuntu/.ssh/authorized_keys
    chmod 600 /home/ubuntu/.ssh/authorized_keys
    chown -R ubuntu:ubuntu /home/ubuntu/.ssh
  EOF

  tags = {
    Name    = "${var.project_name}-app"
    Project = var.project_name
  }
}






















