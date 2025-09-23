package com.itau.thecatapi.credentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

public class SecretsManagerCredentialsProvider implements AwsCredentialsProvider {

    private final SecretsManagerClient secretsManagerClient;
    private final String secretName;
    private final String catApiKey;

    public SecretsManagerCredentialsProvider(SecretsManagerClient secretsManagerClient, String secretName, String catApiKey) {
        this.secretsManagerClient = secretsManagerClient;
        this.secretName = secretName;
        this.catApiKey = catApiKey;
    }

    @Override
    public AwsCredentials resolveCredentials() {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
        String secretString = response.secretString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> secretMap;
        try {
            secretMap = mapper.readValue(secretString, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter segredo para mapa", e);
        }

        return AwsBasicCredentials.create(secretMap.get("accessKey"), secretMap.get("secretKey"));
    }

    public String getApiKey() {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(catApiKey)
                .build();

        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
        String secretString = response.secretString();

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> secretMap = mapper.readValue(secretString, Map.class);
            return secretMap.get("API_KEY");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter segredo para mapa", e);
        }
    }

}


