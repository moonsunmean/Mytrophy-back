pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKERHUB_REPO = 'qpfriday/mytrophy'
        DOCKER_IMAGE_TAG = "${DOCKERHUB_REPO}:${BUILD_NUMBER}"
    }

    stages {
        stage('Git Clone') {
            steps {
                script {
                    // Git 클론
                    sh 'git clone -b master https://kdt-gitlab.elice.io/cloud_track/class_02/web_project3/team04/team4-back.git'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // 빌드
                    sh './gradlew clean build'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Docker 이미지 빌드
                    sh "docker build -t ${DOCKER_IMAGE_TAG} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {
                        // Docker 이미지 푸시
                        sh "docker push ${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }
    }

    post {
        always {
            // Docker 이미지 제거
            sh "docker rmi ${DOCKER_IMAGE_TAG}"
        }
        success {
            // 성공 메시지 출력
            echo 'Docker image pushed to Docker Hub successfully!'
        }
        failure {
            // 실패 메시지 출력
            echo 'Build or push failed.'
        }
    }
}
