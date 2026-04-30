# Copy it to the instance
scp -i ~/.ssh/id_rsa infrastructure/scripts/setup-jenkins.sh ubuntu@<ec2-ip>:~/

# SSH in
ssh -i ~/.ssh/id_rsa ubuntu@<ec2-ip>

# Make it executable and run it
chmod +x setup-jenkins.sh
./setup-jenkins.sh