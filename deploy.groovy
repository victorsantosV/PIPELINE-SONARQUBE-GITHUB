
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
    }
//--------------------------------------------------------------------------------------------
}
