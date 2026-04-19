pipeline {
    agent any

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
    }

    post {
        success {
            echo "Build ${env.BUILD_NUMBER} passed — artifact ready at target/*.jar"
        }
        failure {
            echo "Build ${env.BUILD_NUMBER} failed — check test results above"
        }
    }
}