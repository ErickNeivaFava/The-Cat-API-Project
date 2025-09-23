package com.itau.thecatapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SQSConfig {

    private static final Logger logger = LoggerFactory.getLogger(SQSConfig.class);

    @Value("${spring.cloud.aws.sqs.queue.url}")
    private String breedQueueUrl;

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(Region.SA_EAST_1)
                .build();
    }

    @Bean
    public String breedQueueUrl() {
        return breedQueueUrl;
    }
}
