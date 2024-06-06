pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKERHUB_REPO = 'qpfriday/mytrophy'
        DOCKER_IMAGE_TAG = "${DOCKERHUB_REPO}:${BUILD_NUMBER}"
        SERVER_USERNAME = 'elice' // 사용자 이름
        SERVER_PASSWORD = 'e0w5y63uw7hzte6mwy94ore7ppdis6on' // 비밀번호
        SERVER_IP = '34.64.52.132' // 서버 IP 주소
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

        stage('Docker Pull and Run'){
            steps {
                script {
                    // 서버에서 Docker 이미지를 pull
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'docker pull ${DOCKER_IMAGE_TAG}'"
                    // 중지하고 이미 있는 컨테이너 삭제
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'docker stop mytrophy-service || true && docker rm mytrophy-service || true'"
                    // 새로 받은 컨테이너 실행
                    sh "sshpass -p ${SERVER_CREDENTIALS_PSW} ssh -o StrictHostKeyChecking=no ${SERVER_CREDENTIALS_USR}@${SERVER_IP} 'docker run -d --name mytrophy-service -p 8080:8080 ${DOCKER_IMAGE_TAG}'"
                }
            }
        }
    }
}
