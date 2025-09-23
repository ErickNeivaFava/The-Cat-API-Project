package com.itau.thecatapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TheCatAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheCatAPIApplication.class, args);
	}

}

/*Instruções

1. Utilize o Github para versionar seu projeto;

2. Você tem até a data combinada para concluir as atividades, e o resultado deverá ser enviando impreterivelmente nessa data, independente do ponto do desenvolvimento.

2.1. Foque 1°  em sua zona de conhecimento e progrida o máximo que puder. Caso não consiga concluir todas as atividades, não tem problema, por favor entregue o que foi feito até a data solicitada.

3. Fique à vontade para utilizar tecnologias, frameworks e técnicas não citadas nas atividades.

4. Recomendamos a utilização do Docker (http://www.docker.com) para montagem do ambiente, por facilitar a nossa execução e validação, mas fique à vontade para usar outras ferramentas.

4.1 Caso opte pela utilização do Docker, publique os Dockerfiles no repositório do projeto e imagem Dockerhub, caso necessário.

5. Recomendamos o deploy numa cloud pública por exemplo na AWS (free tier), mas o mais importante é que nós consigamos executar e validar sua infra/código.

6. Critérios de avaliação:

a.       Código e testes unitários: 20%

b.       Estrutura do projeto e Organização 20%

c.       Logging e outros mecanismos de monitoramento: 20%

d.       Documentação: 20%

e.       Facilidade de Deploy: 20%

Obs.: para todos os itens iremos considerar a documentação como parte da entrega.



Case The Cat API

1. Realize um desenho arquitetural informando como sua solução vai funcionar.

2. Crie uma aplicação na Java com framework Spring para coletar as seguintes informações da API de Gatos (https://thecatapi.com/):

·         Para cada uma das raças de gatos disponíveis, armazenar as informações de origem, temperamento e descrição em uma base de dados (se disponível);

·         Para cada uma das raças acima, salvar o endereço de 3 imagens em uma base de dados (se disponível);

·         Salvar o endereço de 3 imagens de gatos com chapéu;

·         Salvar o endereço de 3 imagens de gatos com óculos.



3. Use uma base de dados (ex.: Dynamo AWS, RDS AWS) adequada para armazenar as informações (você terá que justificar o uso dessa base)

4. Utilizando linguagem Java com framework Spring, crie 4 APIs REST, gostaríamos de ver aplicação de threads (processamento paralelo) em um ou mais casos abaixo:

a.       API capaz de listar todas as raças;

b.       API capaz de listar as informações de uma raça;

c.       API capaz de a partir de um temperamento listar as raças;

d.       API capaz de a partir de uma origem listar as raças.



5. Crie uma coleção no Postman ou Insomnia para consumir as APIs criadas (não se esqueça de incluir como parte da entrega)

6. Utilize alguma biblioteca de logging de forma que os eventos gerados sejam identificados corretamente, por exemplo: Warning, Erro, Debug, Info, etc.

7. Publique o projeto no Github e documente em um README.md os itens abaixo:

a.       Documentação do projeto;

b.       Documentação das APIs;

c.       Documentação de arquitetura;

d.       Instruções sobre como podemos subir uma cópia deste ambiente localmente

Bônus - se você chegou até aqui, considere implementar um ou mais itens a seguir:

8. (Bônus) Publique sua API na cloud (Ex.: AWS Fargate).

9. (Bônus) para o item 6 integre a aplicação a alguma ferramenta de Logging (exemplos: AWS Cloudwatch, Elastic Search, Splunk, Graylog ou similar), crie uma query que mostre em tempo real os eventos que acontecem na execução da API criada no item 6, exemplos (Warning, Erro, Debug, Info, etc).

10. (Bônus) para o item 4, implemente as mesmas rotas mas com retorno assíncrono - ou seja, o usuário irá informar um e-mail para o qual as imagens (URL's) devem ser enviadas. Utilize algum mecanismo de enfileiramento que preferir*/