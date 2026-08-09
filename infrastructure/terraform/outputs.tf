output "jenkins_public_ip" {
  description = "Public IP of Jenkins Server"
  value       = aws_instance.jenkins.public_ip
}

output "jenkins_url" {
  description = "Jenkins UI URL"
  value       = "http://${aws_instance.jenkins.public_ip}:8080"
}

output "ssh_command" {
  description = "SSH Command to access Jenkins Server"
  value       = "ssh -i ~/.ssh/id_rsa ubuntu@${aws_instance.jenkins.public_ip}"
}

output ecr_repository_url {
  description = "ECR Repository URL"
  value = aws_ecr_repository.devtracker.repository_url
}

output "app_public_ip" {
  description = "Public IP of the App server"
  value       = aws_instance.app.public_ip
}

output "app_url" {
  description = "App URL"
  value       = "http://${aws_instance.app.public_ip}:8080"
}


