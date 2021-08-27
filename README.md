# PIPELINE-JENKINS-KUBERNETES-DOCKER
Sistema completo de CI/CD usando docker + kubernetes + slack para controle e atualização do seu projeto

## EXPLICAÇÃO DE CADA ETAPA

### Para que voce consiga entender esse projeto, deve-se ter o minimo de conhecimento com o jenkins e estar procurando uma fonte de pesquisa, tendo em vista o vasto conteudo proposto nesse artigo

## CAPTURANDO O PROJETO DO GITHUB E VALIDANDO ELE NO SONARQUBE

  Já dentro da pipeline padrão, onde tudo acontecerá, temos os stages, e dentro do stages, temos o primeiro stage que será responsavel por capturar todo o projeto e utilizamos um plugin do jenkins chamado SONARQUBE, ao qual eh responsavel por analisar e verificar todo o projeto a fim de encontrar vulnerabilidades ou aberturas para ataques de hacking.
  Vale resaltar que todo comando de condicional ou comando usando sh ou DEF ou qualquer um desse tipo , precisa estar dentro de um escopo script:
  
  ![image](https://user-images.githubusercontent.com/37802657/129756534-ff50da9c-a0ed-468d-8ec2-26f22441dcc2.png)
  
