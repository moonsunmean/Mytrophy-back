pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'master',
                    url: 'https://kdt-gitlab.elice.io/cloud_track/class_02/web_project3/team04/team4-back.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def image = docker.build("your-dockerhub-username/your-image-name:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'DOCKERHUB_CREDENTIALS') {
                        def image = docker.build("your-dockerhub-username/your-image-name:${env.BUILD_NUMBER}")
                        image.push()
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}