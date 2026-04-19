variable "aws_region" {
  description = "AWS region to deploy to"
  type        = string
  default     = "af-south-1"
}

variable "project_name" {
  description = "jenkins-devtracker"
  type        = string
  default     = "jenkins-devtracker"
}

variable "instance_type" {
  description = "EC2 instace type"
  type        = string
  default     = "t3.medium"
}

variable "public_key_path" {
  description = "Path to your local SSH public key"
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}
