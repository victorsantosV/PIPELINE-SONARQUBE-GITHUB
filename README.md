# PIPELINE-JENKINS-KUBERNETES-DOCKER
Sistema completo de CI/CD usando docker + kubernetes + slack para controle e atualização do seu projeto

## EXPLICAÇÃO DE CADA ETAPA

## VERIFICANDO A EXISTENCIA DO COMANDO KUBECTL
  Criamos um steps node porque nele conseguimos criar pipelines antes de uma pipeline padrão e nele conseguimos validar comandos ou plugins que precisam iniciar com o projetos: 
  
![image](https://user-images.githubusercontent.com/37802657/129755479-ed508465-822b-4ab2-896c-ce6d81f18f51.png)

## CAPTURANDO O PROJETO DO GITHUB E VALIDANDO ELE NO SONARQUBE

  Já dentro da pipeline padrão, onde tudo acontecerá, temos os stages, e dentro do stages, temos o primeiro stage que será responsavel por capturar todo o projeto e utilizamos um plugin do jenkins chamado SONARQUBE, ao qual eh responsavel por analisar e verificar todo o projeto a fim de encontrar vulnerabilidades ou aberturas para ataques de hacking.
  Vale resaltar que todo comando de condicional ou comando usando sh ou DEF ou qualquer um desse tipo , precisa estar dentro de um escopo script:
  
  ![image](https://user-images.githubusercontent.com/37802657/129756534-ff50da9c-a0ed-468d-8ec2-26f22441dcc2.png)
  
## REALIZANDO UMA VALIDAÇÃO, BUILDANDO e REALIZANDO UM PUSH NA IMAGEM DOCKER

  O processo acima nos dará a possibilidade de criar uma imagem para que possamos usar futuramente no passo do kubernetes e para isso precisamos validar todas as condiçoes do dockerfile que está no projeto usando uma imagem especifica para validaçoes de dockerfile.
  Após as validação, usando
  
