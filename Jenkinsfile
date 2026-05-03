pipeline {
    agent any

    environment {
        AWS_REGION      = "af-south-1"
        ECR_REGISTRY    = "254730583443.dkr.ecr.af-south-1.amazonaws.com"
        ECR_REPO        = "devtracker"
        IMAGE_TAG       = "build-${env.BUILD_NUMBER}"
        FULL_IMAGE_NAME = "${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}"
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
                sh "docker build -t ${FULL_IMAGE_NAME} ."
                sh "docker tag ${FULL_IMAGE_NAME} ${ECR_REGISTRY}/${ECR_REPO}:latest"
            }
        }

        stage('Push to ECR') {
            steps {
                sh """
                    aws ecr get-login-password --region ${AWS_REGION} | \
                    docker login --username AWS --password-stdin ${ECR_REGISTRY}
                """
                sh "docker push ${FULL_IMAGE_NAME}"
                sh "docker push ${ECR_REGISTRY}/${ECR_REPO}:latest"
            }
        }
    }

    post {
        success {
            echo "Build ${env.BUILD_NUMBER} passed — image pushed: ${FULL_IMAGE_NAME}"
        }
        failure {
            echo "Build ${env.BUILD_NUMBER} failed — check logs above"
        }
    }
}