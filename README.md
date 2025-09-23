# The Cat API Project

Este projeto é uma aplicação Java Spring Boot que integra com a The Cat API para coletar e armazenar informações sobre raças de gatos e suas imagens.

## Documentação do Projeto

### Funcionalidades
- Coleta e armazena informações de raças de gatos da The Cat API
- Armazena URLs de imagens de gatos com diferentes categorias (chapéu, óculos)
- Disponibiliza APIs REST para consulta das informações
- Processamento paralelo para melhor performance
- Sistema de logging estruturado

### Tecnologias Utilizadas
- **Java 21** com Spring Boot 3.5.6
- **Spring Data JPA** para persistência
- **PostgreSQL/RDS** como banco de dados principal
- **SQS/ElasticMQ** como mensageria
- **Spring Web** para APIs REST
- **Cloudwatch** para logging
- **ECS/Docker** para containerização
- **Maven** para gerenciamento de dependências

## Documentação de Arquitetura

### Diagrama Arquitetural

```
+-------------------+        +-------------------+        +-------------------+
|    Usuário/API    | <----> |  Load Balancer    | <----> |  ECS + Fargate    |
| (Postman/Frontend)|        | (ALB)             |        | (Spring Boot App) |
+-------------------+        +-------------------+        +-------------------+
                                                              |         |        
                                                              |         |
                                                              v         v
                                                  +----------------+  +----------------+
                                                  | RDS PostgreSQL |  |     SQS        |
                                                  | (Raças, Imagens|  | (Fila Emails)  |
                                                  |  e Metadados)  |  +----------------+
                                                  +----------------+
                                                              ↑
                                                              |
                                                  +----------------------+
                                                  | The Cat API Ingestor |
                                                  | (Job ou Serviço)     |
                                                  | - Consulta externa   |
                                                  | - Processamento      |
                                                  | - Persistência       |
                                                  +----------------------+

```

### Fluxo de Dados
1. A aplicação inicia e dados podem ser carregados através da chamada:
```http
   /api/external/collect-data
```
2. Utiliza threads paralelas para buscar:
    - Informações das raças
    - Imagens por raça
3. Armazena dados no banco de dados
4. Expõe APIs REST para consulta
5. Logs são enviados para console e Cloudwatch

## Documentação das APIs

### Endpoints Disponíveis

#### 1. Listar Todas as Raças
```http
GET /api/breeds
```
**Response:**
```json
[
  {
    "id": "string",
    "name": "string",
    "origin": "string",
    "temperament": "string",
    "description": "string",
    "imageUrl": "string"
  }
]
```

#### 2. Obter Informações de uma Raça
```http
GET /api/breeds/{breedId}
```
**Response:**
```json
{
   "id": "string",
   "name": "string",
   "origin": "string",
   "temperament": "string",
   "description": "string", 
   "imageUrl": "string"
}
```

#### 3. Listar Raças por Temperamento
```http
GET /api/breeds?temperament={temperament}
```
**Response:**
```json
[
  {
    "id": "string",
    "name": "string",
    "origin": "string",
    "temperament": "string"
  }
]
```

#### 4. Listar Raças por Origem
```http
GET /api/breeds?origin={origin}
```
**Response:**
```json
[
  {
    "id": "string",
    "name": "string",
    "origin": "string",
    "temperament": "string"
  }
]
```

### Códigos de Status HTTP
- 200 OK: Requisição bem-sucedida
```json

```

- 404 Not Found: Recurso não encontrado
```json
{
   "status": "int",
   "error": "string",
   "message": "string",
   "path": "string",
   "timestamp": "LocalDateTime"
}
```

- 500 Internal Server Error: Erro interno do servidor
```json
{
"status": "int",
"error": "string",
"message": "string",
"path": "string",
"timestamp": "LocalDateTime"
}
```

## Como Configurar a Chave da API
- Acesse https://thecatapi.com/
- A partir do cadastro, uma chave é recebida no e-mail
- Altere a propriedade `thecatapi.api-key` em `application.properties` com sua chave


## Como Executar Localmente

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6+
- Docker e Docker Compose (opcional)
- É altamente recomendado configurar uma chave de API

### Método 1: Usando Docker (Recomendado)

1. **Clone o repositório:**
```bash
git clone https://github.com/ErickNeivaFava/The-Cat-API-Project
cd the-cat-api-project
```

2. **Execute com Docker Compose:**
```bash
docker-compose up -d
```

3. **A aplicação estará disponível em:** http://localhost:8080

### Método 2: Execução Local

1. **Clone o repositório:**
```bash
git clone https://github.com/ErickNeivaFava/The-Cat-API-Project
cd the-cat-api-project
```

2. **Compile o projeto:**
```bash
mvn clean package
```

3. **Execute a aplicação:**
```bash
java -jar target/the-cat-api-project-0.0.1-SNAPSHOT.jar
```

4. **Ou execute com Maven:**
```bash
mvn spring-boot:run
```

### Configuração

As variáveis de ambiente podem ser configuradas no arquivo `application.properties` ou via environment variables:

```properties
# The Cat API Configuration
thecatapi.base-url=https://api.thecatapi.com/v1
thecatapi.api-key=INSIRA_SUA_API_KEY

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/thecatapi
spring.datasource.username=postgres
spring.datasource.password=password

# Logging
logging.level.com.yourpackage=DEBUG
```

## Logging e Monitoramento

A aplicação utiliza Logger. Os logs são gerados em:

- Console (formato legível)

### Níveis de Log
- **INFO**: Operações normais
- **DEBUG**: Informações detalhadas para debugging
- **WARN**: Situações potencialmente problemáticas
- **ERROR**: Erros que exigem atenção

## Testes

Execute os testes com:
```bash
mvn test
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/itau/thecatapi/
│   │   ├── controller/       # APIs REST
│   │   ├── service/          # Lógica de negócio
│   │   ├── repository/       # Camada de persistência
│   │   ├── model/            # Entidades JPA
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── config/           # Configurações Spring
│   │   ├── client/           # Integração com The Cat API
│   │   ├── exception/        # Exceções customizadas e handlers
│   │   ├── utils/            # Utilitários e helpers
│   │   └── TheCatApiIntegrationApplication.java
│   └── resources/
│       └── application.properties
└── test/                    # Testes unitários
```

## Configuração do Banco de Dados

O projeto utiliza **PostgreSQL** como banco principal pelas seguintes razões:
- Suporte robusto a operações relacionais
- Performance para consultas complexas
- Suporte nativo a JSON quando necessário
- Comunidade ativa e ampla adoção

## Coleção do Postman

A coleção do Postman está disponível em:
`/postman/The_Cat_API_Collection.json`

Importe este arquivo no Postman para testar todas as APIs.

## Deploy na Nuvem

### Opção AWS Fargate (Bônus)
1. Deploy feito automaticamente através de Git Actions
2. Terraform para provisionamento

## Suporte

Para issues ou dúvidas, abra uma issue no repositório GitHub ou entre em contato através do email: erickneivafava@gmail.com