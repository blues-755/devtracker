output "jenkins_public_ip" {
  description = "Public IP of Jenkins Server"
  value       = aws_instance.jenkins.public_ip
}

output "jenkins_url" {
  description = "Jenkins UI URL"
  value       = "http://${aws_instance.jenkins.public_ip}:8080"
}
//Comment
output "ssh_command" {
  description = "SSH Command to access Jenkins Server"
  value       = "ssh -i ~/.ssh/id_rsa ubuntu@${aws_instance.jenkins.public_ip}"
}

