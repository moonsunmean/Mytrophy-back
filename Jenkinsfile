pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKERHUB_REPO = 'qpfriday/mytrophy'
    }

    stages {
        stage('Clone repository') {
            stage('Clone Repository') {
                steps {
                    git branch: 'master',
                        url: 'https://kdt-gitlab.elice.io/cloud_track/class_02/web_project3/team04/team4-back.git'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Docker 빌드
                    sh 'docker build -t $DOCKERHUB_REPO:$BUILD_NUMBER .'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'DOCKERHUB_CREDENTIALS') {
                        // Docker 이미지를 푸시
                        sh 'docker push $DOCKERHUB_REPO:$BUILD_NUMBER'
                    }
                }
            }
        }
    }

    post {
        always {
            // 작업이 완료되면 Docker 이미지 제거
            sh 'docker rmi $DOCKERHUB_REPO:$BUILD_NUMBER'
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
