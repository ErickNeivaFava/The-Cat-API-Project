package com.itau.thecatapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.thecatapi.message.BreedRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class SQSMessagingService {

    private static final Logger logger = LoggerFactory.getLogger(SQSMessagingService.class);

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public SQSMessagingService(SqsAsyncClient sqsAsyncClient,
                               ObjectMapper objectMapper,
                               @Value("${spring.cloud.aws.sqs.queue.url}") String queueUrl) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    public CompletableFuture<Void> sendBreedRequest(BreedRequestMessage message) {
        try {
            message.setMessageId(UUID.randomUUID().toString());

            String messageBody = objectMapper.writeValueAsString(message);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .messageGroupId(message.getEmail())
                    .build();

            return sqsAsyncClient.sendMessage(request)
                    .thenAccept(response -> {
                        logger.info("Mensagem enviada para SQS. MessageID: {}, SQSMessageId: {}, Email: {}",
                                message.getMessageId(), response.messageId(), message.getEmail());
                    })
                    .exceptionally(throwable -> {
                        logger.error("Erro ao enviar mensagem para SQS: {}", throwable.getMessage(), throwable);
                        throw new RuntimeException("Falha no envio para SQS", throwable);
                    });

        } catch (Exception e) {
            logger.error("Erro ao serializar mensagem para SQS: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(new RuntimeException("Falha na serialização da mensagem", e));
        }
    }

    public void sendBreedRequestSync(BreedRequestMessage message) {
        try {
            sendBreedRequest(message).join();
        } catch (Exception e) {
            throw new RuntimeException("Falha no envio síncrono para SQS", e);
        }
    }
}