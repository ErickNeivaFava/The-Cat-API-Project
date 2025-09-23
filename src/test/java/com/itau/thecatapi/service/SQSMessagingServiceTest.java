package com.itau.thecatapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.thecatapi.message.BreedRequestMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQSMessagingServiceTest {

    private SqsAsyncClient sqsAsyncClient;
    private ObjectMapper objectMapper;

    private SQSMessagingService messagingService;

    private final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";

    @BeforeEach
    void setUp() {
        sqsAsyncClient = mock(SqsAsyncClient.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        messagingService = new SQSMessagingService(sqsAsyncClient, objectMapper, QUEUE_URL);
    }

    @Test
    void testSendBreedRequestSuccess() throws Exception {
        BreedRequestMessage message = new BreedRequestMessage();
        message.setEmail("test@example.com");
        //message.setRequestTime(LocalDateTime.now());

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("sqs-message-id")
                .build();

        CompletableFuture<SendMessageResponse> future = CompletableFuture.completedFuture(response);
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        CompletableFuture<Void> result = messagingService.sendBreedRequest(message);

        assertDoesNotThrow(result::join);
        verify(sqsAsyncClient, times(1)).sendMessage(any(SendMessageRequest.class));
        assertNotNull(message.getMessageId());
    }

    @Test
    void testSendBreedRequestSerializationError() {
        ObjectMapper brokenMapper = mock(ObjectMapper.class);
        messagingService = new SQSMessagingService(sqsAsyncClient, brokenMapper, QUEUE_URL);

        BreedRequestMessage message = new BreedRequestMessage();
        message.setEmail("test@example.com");

        try {
            when(brokenMapper.writeValueAsString(any())).thenThrow(new RuntimeException("Serialization error"));
        } catch (Exception ignored) {}

        CompletableFuture<Void> result = messagingService.sendBreedRequest(message);

        assertTrue(result.isCompletedExceptionally());
    }

    @Test
    void testSendBreedRequestSyncSuccess() {
        BreedRequestMessage message = new BreedRequestMessage();
        message.setEmail("test@example.com");

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("sqs-message-id")
                .build();

        CompletableFuture<SendMessageResponse> future = CompletableFuture.completedFuture(response);
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        assertDoesNotThrow(() -> messagingService.sendBreedRequestSync(message));
    }

    @Test
    void testSendBreedRequestSyncFailure() {
        BreedRequestMessage message = new BreedRequestMessage();
        message.setEmail("test@example.com");

        CompletableFuture<SendMessageResponse> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("SQS failure"));

        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> messagingService.sendBreedRequestSync(message));
        assertTrue(exception.getMessage().contains("Falha no envio síncrono para SQS"));
    }
}

