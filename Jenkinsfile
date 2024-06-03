pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKERHUB_REPO = 'qpfriday/mytrophy'
        DOCKER_IMAGE_TAG = "${env.DOCKERHUB_REPO}:${env.BUILD_NUMBER}"
    }

    stages {

        stage('git clone') {
            steps {
                // git clone
                git branch: 'dev', credentialsId: 'gitlab-credentials-id', url: 'https://kdt-gitlab.elice.io/cloud_track/class_02/web_project3/team04/team4-back.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    // Set gradlew permissions
                    sh 'chmod +x gradlew'
                    // Build
                    sh './gradlew clean build'
                }
            }
        }

        stage('Verify Docker Installation') {
            steps {
                script {
                    // Verify Docker installation
                    sh 'docker --version'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    sh "docker build -t ${DOCKER_IMAGE_TAG} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    // Push Docker image to Docker Hub
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {
                        sh "docker push ${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }
    }
}
