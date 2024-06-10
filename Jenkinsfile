pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKERHUB_REPO = 'qpfriday/mytrophy'
        DOCKER_IMAGE_TAG = "${DOCKERHUB_REPO}:${BUILD_NUMBER}"
        SERVER_CREDENTIALS = credentials('server-credentials-id')
        SERVER_IP = '34.64.52.132'
    }

    stages {
        stage('git clone') {
            steps {
                // git 클론
                git branch: 'dev', credentialsId: 'gitlab-credentials-id', url: 'https://kdt-gitlab.elice.io/cloud_track/class_02/web_project3/team04/team4-back.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    // 빌드
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build'
                }
            }
        }

        stage('Verify Docker Installation') {
            steps {
                script {
                    // docker 버전 확인
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

        stage('Docker Login'){
            steps{
                script {
                    // docker 로그인
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/') {
                        // docker hub 이미지 푸시
                        sh "docker push ${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }

        stage('SSH to Server'){
            steps {
                script {
                    // SSH로 서버에 연결하여 명령 실행
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'whoami'"
                }
            }
        }

        stage('Remove Previous Docker Container'){
            steps {
                script {
                    // 이전 컨테이너 중지 및 삭제
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'docker stop mytrophy-service || true && docker rm mytrophy-service || true'"
                }
            }
        }

        stage('Remove Old Docker Image'){
            steps {
                script {
                    // 이전 이미지 삭제
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'docker rmi -f ${DOCKERHUB_REPO}:latest'"
                }
            }
        }

        stage('Pull New Docker Image'){
            steps {
                script {
                    // 새 이미지 pull
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'docker pull ${DOCKER_IMAGE_TAG}'"
                }
            }
        }

        stage('Run Docker Container'){
            steps {
                script {
                    // 새로운 컨테이너 실행
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'docker run -d --name mytrophy-service -p 8080:8080 ${DOCKER_IMAGE_TAG}'"
                }
            }
        }
    }
}
