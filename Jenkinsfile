pipeline {
    agent any
    environment {
        FRONTEND_API_URL = 'http://localhost:3000'
        FRONTEND_KEYCLOAK_URL = 'http://localhost:8080'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ie1g20/wordle.git'
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh "yarn install"
                    sh "yarn build"
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose down'
                sh 'docker-compose up -d --build'
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }
    }
}
