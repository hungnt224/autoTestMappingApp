pipeline {
    agent { label 'automationtest' }

    stages {
        stage('Build & Run Test') {
            steps {
                dir('C:/Users/HungNT/Desktop/seminar_t5/mappingApp/autoTestMappingApp')
                {bat 'mvn clean test -o'}
            }
        }
    }

    post {
        always {
            allure results: [[path: 'C:/Users/HungNT/Desktop/seminar_t5/mappingApp/autoTestMappingApp/target/allure-result']]
    }
}
}
