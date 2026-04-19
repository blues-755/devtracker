pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        // DB vars only needed for running the app, not tests (tests use H2)
        DB_URL  = credentials('db-url')
        DB_USER = credentials('db-user')
        DB_PASS = credentials('db-pass')
    }

    tools {
        maven 'Maven-3.9'   // Name must match what you configure in Jenkins Global Tools
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
                // Tests run against H2 — no real DB needed here
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