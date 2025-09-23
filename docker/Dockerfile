# Etapa 1: build com Maven
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia o pom.xml e resolve dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o restante do projeto e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagem final com JDK 21
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia o JAR gerado da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
