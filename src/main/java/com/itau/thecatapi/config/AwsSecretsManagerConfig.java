package com.itau.thecatapi.config;

import com.itau.thecatapi.credentials.SecretsManagerCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class AwsSecretsManagerConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String awsRegion;

    @Value("${aws.secret.name}")
    private String secretName;

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()

                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(SecretsManagerClient secretsManagerClient) {
        return new SecretsManagerCredentialsProvider(secretsManagerClient, secretName);
    }
}
