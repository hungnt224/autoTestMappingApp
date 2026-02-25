pipeline {
    agent { label 'autoTest' }

    environment {
        PROJECT_PATH = 'C:\\resourcetest'
    }

    stages {

        stage('Prepare') {
            steps {
                echo "Using local automation project at: ${PROJECT_PATH}"
                dir("${PROJECT_PATH}") {
                    bat 'mvn -v'
                }
            }
        }

        stage('Run Automation Test') {
            steps {
                dir("${PROJECT_PATH}") {
                    bat 'mvn clean test'
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                dir("${PROJECT_PATH}") {
                    allure includeProperties: false,
                           jdk: '',
                           results: [[path: 'target/allure-results']]
                }
            }
        }
    }

    post {
        always {
            echo "Build finished."
        }
        success {
            echo "All tests passed."
        }
        failure {
            echo "Test failed. Check Allure report."
        }
    }
}
