pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKERHUB_REPO = 'qpfriday/chandol'
        DOCKER_IMAGE_TAG = "${DOCKERHUB_REPO}:${BUILD_NUMBER}"
    }

    stages {
        stage('Prepare Environment') {
            steps {
                script {
                    // Clean up the existing directory if it exists
                    sh 'rm -rf jenkinsTest'
                }
            }
        }

        stage('Git Clone') {
            steps {
                script {
                    // Git clone
                    sh 'git clone -b master https://kdt-gitlab.elice.io/cloud_track/class_02/web_project3/team04/team4-back.git'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // Navigate to the cloned directory
                    dir('jenkinsTest') {
                        // Set execute permissions for gradlew
                        sh 'chmod +x gradlew'
                        // Build
                        sh './gradlew clean build'
                    }
                }
            }
        }

        stage('Verify Docker Installation') {
            steps {
                script {
                    // Ensure Docker is installed and accessible
                    sh 'docker --version'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Docker image build
                    sh "docker build -t ${DOCKER_IMAGE_TAG} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/') {
                        // Docker image push
                        sh "docker push ${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }
    }
}
