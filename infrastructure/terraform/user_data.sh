#!/bin/bash
set -e

echo "── Updating packages ──────────────────────────────"
apt-get update -y
apt-get upgrade -y

echo "── Installing Java 17 ─────────────────────────────"
apt-get install -y openjdk-17-jdk

echo "── Installing Maven ───────────────────────────────"
apt-get install -y maven

echo "── Installing Jenkins ─────────────────────────────"
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | \
  gpg --dearmor -o /usr/share/keyrings/jenkins-keyring.gpg

echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.gpg] \
  https://pkg.jenkins.io/debian-stable binary/" | \
  tee /etc/apt/sources.list.d/jenkins.list > /dev/null

apt-get update -y
apt-get install -y jenkins

echo "── Installing Git ─────────────────────────────────"
apt-get install -y git

echo "── Installing Docker ──────────────────────────────"
apt-get install -y ca-certificates curl gnupg
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo "deb [arch=$(dpkg --print-architecture) \
  signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io

usermod -aG docker jenkins
usermod -aG docker ubuntu

echo "── Starting services ──────────────────────────────"
systemctl enable jenkins
systemctl start jenkins
systemctl enable docker
systemctl start docker

echo "── Done ───────────────────────────────────────────"