# PIPELINE-JENKINS-KUBERNETES-DOCKER
Sistema completo de CI/CD usando docker + kubernetes + slack para controle e atualização do seu projeto

## EXPLICAÇÃO DE CADA ETAPA

### Para que voce consiga entender esse projeto, deve-se ter o minimo de conhecimento com o jenkins e estar procurando uma fonte de pesquisa, tendo em vista o vasto conteudo proposto nesse artigo

## VERIFICANDO A EXISTENCIA DO COMANDO KUBECTL
  Criamos um steps node porque nele conseguimos criar pipelines antes de uma pipeline padrão e nele conseguimos validar comandos ou plugins que precisam iniciar com o projetos: 
  
![image](https://user-images.githubusercontent.com/37802657/129755479-ed508465-822b-4ab2-896c-ce6d81f18f51.png)

## CAPTURANDO O PROJETO DO GITHUB E VALIDANDO ELE NO SONARQUBE

  Já dentro da pipeline padrão, onde tudo acontecerá, temos os stages, e dentro do stages, temos o primeiro stage que será responsavel por capturar todo o projeto e utilizamos um plugin do jenkins chamado SONARQUBE, ao qual eh responsavel por analisar e verificar todo o projeto a fim de encontrar vulnerabilidades ou aberturas para ataques de hacking.
  Vale resaltar que todo comando de condicional ou comando usando sh ou DEF ou qualquer um desse tipo , precisa estar dentro de um escopo script:
  
  ![image](https://user-images.githubusercontent.com/37802657/129756534-ff50da9c-a0ed-468d-8ec2-26f22441dcc2.png)
  
## REALIZANDO UMA VALIDAÇÃO, BUILDANDO e REALIZANDO UM PUSH NA IMAGEM DOCKER

  O processo acima nos dará a possibilidade de criar uma imagem para que possamos usar futuramente no passo do kubernetes e para isso precisamos validar todas as condiçoes do dockerfile que está no projeto usando uma imagem especifica para validaçoes de dockerfile.
  Após as validação, usando o comando docker build via sh:
  
![image](https://user-images.githubusercontent.com/37802657/129757545-70ebf952-970d-45e8-abc0-48f88da99abd.png)

  Conseguimos realizar um build, ou seja, criamos a imagem atualizada.
  Agora precisamos mandar ela para o docker hub, ou seja, precisamos de uma conta Docker. Tendo em vista que ja possuimos essa conta, seguimos igual a essa estagio abaixo: 
  
  ![image](https://user-images.githubusercontent.com/37802657/129757961-3a52204a-891c-47d3-b648-dd012f5b1369.png)

## USANDO COMANDOS KUBERNETES NA PIPELINE
  Após terminarmos o estagio envolvendo docker, entramos no ato do kubernetes, onde atualizamos somente a imagem, o que faz com que o projeto, nao precise toda vez realizar um build uma vez que as configuraçoes de build nao irao ser modificadas e sim a imagem :
  
  ![image](https://user-images.githubusercontent.com/37802657/129758243-68bd0e22-94c8-4e87-8703-8ca2301fa892.png)

  Na imagem, nós atualizamos a imagem e anotamos no sistema qual foi a versão que atualizamos para que assim, possamos realizar um roolback caso essa versão nao esteja bem desenvolvida ou esteja com algum problema
  
 ## TESTANDO A SEGURANCA DA SUA APLICAÇÃO FINAL
 
  Depois de todos esse projetos descritos acima, realizamos um ataque de força bruta proposital, usando um plugin chamado WAPITI. Caso haja falhas nos retornamos para a versão anterior a essa, por quetão de segurança:
  
  ![image](https://user-images.githubusercontent.com/37802657/129758985-2c82edf2-1954-4013-af05-1a630af6db0f.png)

Caso Tudo ocorra bem, enviamos uma mensagem de sucesso para o slack
