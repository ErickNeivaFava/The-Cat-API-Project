package com.itau.thecatapi.service.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Profile("aws")
@Service
public class AwsQueueService implements QueueService {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public AwsQueueService(SqsClient sqsClient, @Value("${spring.cloud.aws.sqs.queue.url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }

    @Override
    public void sendMessage(String message) {
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build());
    }
}

