node {
        stage("Verify kubectl"){
            if("./kubectl" == "NOT FOUND"){
                sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/v1.20.5/bin/linux/amd64/kubectl" '
                sh 'chmod 777 ./kubectl'
            }else{
                echo "Sistema possui kubectl"
            }
        }
    }
//--------------------------------------------------------------------------------------------
pipeline {
    agent any
    environment {
        // COLOCAR TODAS AS VARIAVEIS DE AMBIENTE QUE ESTÃO NO CODIGO NESSE ESCOPO
    }
//-------------------------------------------------------------------------------------------- 
  stages {
        stage("Get Link GITHUB  and Test with SonaQube") {
            steps {
                git url: "${LINK_SSH_GITHUB}", branch: "main", credentialsId: 'github'
                withSonarQubeEnv('sonarqube') {
                    sh  " ${tool('sonarqube')}/bin/sonar-scanner \
                        -D sonar.projectKey=${NAME_PROJECT_ANALISER} \
                        -D sonar.sources=. \
                        -D sonar.host.url=${URL_SONARQUBE} \
                        -D sonar.login=${TOKEN_SONARQUBE} "
                }
            script {
                if("$RESULT_STATUS" == "SUCCESS"){
                    echo "Passando para proximo estágio"
                }else{
                    slackSend color: "good", message: "Jenkins Realizou um teste usando o sonarcube e obteve erro"
                }
              }
            }
        }
//-------------------------------------------------------------------------------------------- 
        stage("Test Image Dockerfile") {
            steps {
                sh 'docker run --rm -i ghcr.io/hadolint/hadolint:v2.6.0-debian < Dockerfile'
                script {
                if("$RESULT_STATUS" == "SUCCESS") {
                    echo "Passando para proximo estágio"
                }else{
                    slackSend color: "good", message: "Jenkins realizou um teste na imagem e obteve um erro"
                }
              }
            }
        }
//--------------------------------------------------------------------------------------------
        stage("Docker Build") {
            steps {
                jenkinfilesh 'docker build -t ${NAME_IMAGE} .'
            script {
                if("$RESULT_STATUS" == "SUCCESS") {
                    echo "Passando para proximo estágio"
                }else{
                     slackSend color: "good", message: "Jenkins realizou um build mas obteve erro"
                }
              }
            }
        }
//-------------------------------------------------------------------------------------------
        stage("Docker Push") {
            steps {
                sh 'docker login -u ${USER_DOCKER} -p ${SECRET_DOCKERHUB}'
                sh 'docker push ${NAME_IMAGE}'
            script {
                if("$RESULT_STATUS" == "SUCCESS") {
                    echo "Passando para proximo estágio"
                }else{
                    slackSend color: "good", message: "Jenkins realizou um docker push mas obteve erro"
                }
              }
            }
        }
//-------------------------------------------------------------------------------------------
        stage("Update Image to Deploy") {
            steps {
                script {
                        try{
                            sh "./kubectl set image deployment/${JOB_NAME}-dev ${JOB_NAME}-dev=${NAME_IMAGE} --record=true"
                            sh "./kubectl annotate deploy ${JOB_NAME}-dev kubernetes.io/change-cause='mudando para versão ${NAME_IMAGE}' "
                            sh "./kubectl rollout restart deploy ${JOB_NAME}-dev "
                        } catch(e) {
                            sh "./kubectl rollout undo deploy ${JOB_NAME}-dev "
                        }
                    }
                }
            }
//-------------------------------------------------------------------------------------------- 
        stage("Init Pentest") {
            steps {
                script {
                        try {
                            sh( script: 'wapiti -u ${URL_TARGET_WAPITI} -f txt')
                                if("$RESULT_STATUS" == "SUCCESS") {
                                  slackUploadFile(color:"good", channel: "#${YOUR_CHANNEL}", filePath: "*.txt", initialComment:  "Resultado teste de seguranca.")
                                    sh 'rm *.txt'
                                } else {
                                    slackSend color: "red", message: "Wapiti realizou o pentest mas obteve erro"
                                }
                    } catch (e) {
                        echo "erro de pentest"
                    }
                }
            }
        }  
//--------------------------------------------------------------------------------------------
        stage("Send Message to Discord App") {
            steps {
             slackSend(channel: "#${YOUR_CHANNEL}", 
             color: "good", 
             message: "kubernetes realizou um deploy ! , IMAGEM = ${NAME_IMAGE} , Deploy = ${JOB_NAME}, RESULT = ${RESULT_STATUS}")
            }
        }
//--------------------------------------------------------
        stage("Send Config to product") {
            steps {
             slackSend(channel: "#${YOUR_CHANNEL}", 
             color: "good", 
             message: "kubernetes realizou um deploy para produção ! , IMAGEM = ${NAME_IMAGE} , Deploy = ${JOB_NAME}, RESULT = ${RESULT_STATUS}")
            }
        }
        stage("Update image do prod") {
            steps{
                script {
                    try{
                        sh "./kubectl set image deployment/${JOB_NAME}-prod ${JOB_NAME}-prod=${NAME_IMAGE} --record=true"
                        sh "./kubectl annotate deploy ${JOB_NAME}-prod kubernetes.io/change-cause='mudando para versão ${NAME_IMAGE}' "
                        sh "./kubectl rollout restart deploy ${JOB_NAME}-prod "
                    } catch(e) {
                        sh "./kubectl rollout undo deploy ${JOB_NAME}-prod "
                    }
                }
            }
        }
    }
//--------------------------------------------------------------------------------------------
}
