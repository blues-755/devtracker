#!/bin/bash
set -e

echo "── Updating packages ──────────────────────────────"
apt-get update -y
apt-get upgrade -y

echo "── Installing Java 21 ─────────────────────────────"
apt-get install -y openjdk-21-jdk

echo "── Installing Maven ───────────────────────────────"
apt-get install -y maven

echo "── Installing Git ─────────────────────────────────"
apt-get install -y git

echo "── Installing Docker ──────────────────────────────"
apt-get install -y ca-certificates curl gnupg
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io

echo "── Downloading Jenkins ────────────────────────────"
mkdir -p /opt/jenkins
wget -q https://get.jenkins.io/war-stable/latest/jenkins.war -O /opt/jenkins/jenkins.war

echo "── Setting up Jenkins user ────────────────────────"
useradd -m -d /var/lib/jenkins jenkins 2>/dev/null || true
mkdir -p /var/lib/jenkins
chown -R jenkins:jenkins /var/lib/jenkins

echo "── Adding users to Docker group ───────────────────"
usermod -aG docker jenkins
usermod -aG docker ubuntu

echo "── Installing AWS CLI ─────────────────────────────"
apt-get install -y awscli

echo "── Creating Jenkins systemd service ───────────────"
tee /etc/systemd/system/jenkins.service << 'EOF'
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

echo "── Starting services ──────────────────────────────"
systemctl daemon-reload
systemctl enable jenkins
systemctl start jenkins
systemctl enable docker
systemctl start docker

echo "── Done ───────────────────────────────────────────"