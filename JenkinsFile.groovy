pipeline {
    agent { label 'automationtest' }

    environment {
        PROJECT_PATH = 'C:\\resourcetest'
    }

    stages {

        stage('Checkout Source') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-creds',   // nếu repo private
                    url: 'https://github.com/hungnt224/autoTestMappingApp.git'
            }
        }

        stage('Build & Run Test') {
            steps {
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            allure(
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
            )
    }
}
}
