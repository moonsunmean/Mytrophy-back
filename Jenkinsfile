pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKERHUB_REPO = 'qpfriday/mytrophy'
        DOCKER_IMAGE_TAG = "${DOCKERHUB_REPO}:${BUILD_NUMBER}"
        GIT_CREDENTIALS = credentials('GitLabUsernamePW')
    }

    stages {
        stage('Git Clone') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'GitLabUsernamePW', usernameVariable: 'qpfriday@gmail.com', passwordVariable: 'cksgur15617!')]) {
                    script {
                        // Use the GitLab credentials for cloning the repository
                        sh "git clone -b dev https://${GIT_USERNAME}:${GIT_PASSWORD}@kdt-gitlab.elice.io/cloud_track/class_02/web_project3/team04/team4-back.git"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // gradlew 권한 설정
                    sh 'chmod +x gradlew'
                    // 빌드
                    sh './gradlew clean build'
                }
            }
        }

        stage('Verify Docker Installation') {
            steps {
                script {
                    // docker 설치 확인
                    sh 'docker --version'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // docker 이미지 빌드
                    sh "docker build -t ${DOCKER_IMAGE_TAG} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {
                        // docker 이미지 푸시
                        sh "docker push ${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }
    }
}
