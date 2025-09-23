package com.itau.thecatapi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String awsRegion;

    @Value("${aws.secret.name}")
    private String secretName;

    @Value("${spring.datasource.url}")
    private String url;

    @Bean
    public DataSource dataSource() {
        SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
                .region(Region.of(awsRegion))
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
        String secretString = response.secretString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> secretMap;
        try {
            secretMap = mapper.readValue(secretString, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao ler segredo do Secrets Manager", e);
        }

//        String url = String.format("jdbc:postgresql://%s:%s/%s",
//                secretMap.get("host"),
//                secretMap.get("port"),
//                secretMap.get("dbname"));

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(secretMap.get("username"));
        config.setPassword(secretMap.get("password"));
        config.setDriverClassName("org.postgresql.Driver");

        return new HikariDataSource(config);
    }
}

