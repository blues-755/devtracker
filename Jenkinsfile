pipeline {
    agent any

    environment {
        IMAGE_NAME = "devtracker"
        IMAGE_TAG  = "build-${env.BUILD_NUMBER}"
    }

    tools {
        maven 'Maven-3.9'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -q'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -q'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }
    }

    post {
        success {
            echo "Build ${env.BUILD_NUMBER} passed — Docker image: ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "Build ${env.BUILD_NUMBER} failed — check logs above"
        }
    }
}