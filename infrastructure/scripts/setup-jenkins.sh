#!/bin/bash
set -e

echo "──────────────────────────────────────────"
echo " Jenkins Setup Script"
echo "──────────────────────────────────────────"

echo " Step 1: Updating packages..."
sudo apt-get update -y

echo " Step 2: Installing Java 17..."
sudo apt-get install -y openjdk-17-jdk

echo " Step 3: Installing Maven..."
sudo apt-get install -y maven

echo " Step 4: Installing Git..."
sudo apt-get install -y git

echo " Step 5: Installing Docker..."
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) \
  signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

echo " Step 6: Downloading Jenkins..."
sudo mkdir -p /opt/jenkins
sudo wget -q https://get.jenkins.io/war-stable/latest/jenkins.war -O /opt/jenkins/jenkins.war

echo " Step 7: Setting up Jenkins user and directories..."
sudo useradd -m -d /var/lib/jenkins jenkins 2>/dev/null || echo "User already exists"
sudo mkdir -p /var/lib/jenkins
sudo chown -R jenkins:jenkins /var/lib/jenkins

echo " Step 8: Adding users to Docker group..."
sudo usermod -aG docker jenkins
sudo usermod -aG docker ubuntu

echo " Step 9: Creating Jenkins systemd service..."
sudo tee /etc/systemd/system/jenkins.service << 'EOF'
[Unit]
Description=Jenkins Continuous Integration Server
After=network.target

[Service]
Type=simple
User=jenkins
Environment="JENKINS_HOME=/var/lib/jenkins"
ExecStart=/usr/bin/java -jar /opt/jenkins/jenkins.war --httpPort=8080
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

echo " Step 10: Starting Jenkins..."
sudo systemctl daemon-reload
sudo systemctl enable jenkins
sudo systemctl start jenkins
sudo systemctl enable docker
sudo systemctl start docker

echo ""
echo "──────────────────────────────────────────"
echo " Setup Complete!"
echo " Jenkins will be ready in ~60 seconds"
echo " URL: http://$(curl -s https://checkip.amazonaws.com):8080"
echo ""
echo " Initial admin password:"
sleep 30
sudo cat /var/lib/jenkins/secrets/initialAdminPassword 2>/dev/null || \
  echo "Not ready yet — run: sudo cat /var/lib/jenkins/secrets/initialAdminPassword"
echo "──────────────────────────────────────────"