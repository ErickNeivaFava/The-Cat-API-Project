package com.itau.thecatapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.thecatapi.dto.BreedResponseDTO;
import com.itau.thecatapi.message.BreedRequestMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQSMessageListenerTest {

    @InjectMocks
    private SQSMessageListener listener;

    @Mock
    private BreedService breedService;

    @Mock
    private BreedImageService breedImageService;

    @Mock
    private EmailService emailService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveMessage_withValidJson_shouldProcessBreedRequest() throws Exception {
        String json = "{\"messageId\":\"123\",\"email\":\"test@example.com\",\"breedId\":\"abc\"}";
        BreedRequestMessage request = new BreedRequestMessage();
        request.setMessageId("123");
        request.setEmail("test@example.com");
        request.setBreedId("abc");

        when(objectMapper.readValue(json, BreedRequestMessage.class)).thenReturn(request);
        doNothing().when(emailService).sendBreedInfoEmail(any(), any(), any(), any());

        BreedResponseDTO breed = new BreedResponseDTO("abc", "Siamese", "Thailand", "Friendly", "Elegant cat", "https://cdn2.thecatapi.com/images/WmBbMJmwn.jpg");
        when(breedService.getBreedById("abc")).thenReturn(CompletableFuture.completedFuture(breed));
        when(breedImageService.getImageUrlsByBreedId("abc", 3)).thenReturn(CompletableFuture.completedFuture(List.of("url1", "url2", "url3")));

        listener.receiveMessage(json);

        verify(emailService).sendBreedInfoEmail(eq("test@example.com"), any(), contains("Siamese"), any());
    }

    @Test
    void testReceiveMessage_withInvalidJson_shouldThrowException() throws Exception {
        String invalidJson = "invalid";

        when(objectMapper.readValue(anyString(), eq(BreedRequestMessage.class)))
                .thenThrow(new RuntimeException("Erro de parsing"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            listener.receiveMessage(invalidJson);
        });

        assertTrue(exception.getMessage().contains("Falha no processamento"));
    }

    @Test
    void testProcessBreedInformation_withBreedId() {
        BreedRequestMessage request = new BreedRequestMessage();
        request.setBreedId("abc");

        BreedResponseDTO breed = new BreedResponseDTO("abc", "Siamese", "Thailand", "Friendly", "Elegant cat", "https://cdn2.thecatapi.com/images/WmBbMJmwn.jpg");
        when(breedService.getBreedById("abc")).thenReturn(CompletableFuture.completedFuture(breed));

        String result = listener.processBreedInformation(request);

        assertTrue(result.contains("Raça: Siamese"));
        assertTrue(result.contains("Origem: Thailand"));
    }

    @Test
    void testGetImagesForRequest_withBreedId() {
        BreedRequestMessage request = new BreedRequestMessage();
        request.setBreedId("abc");

        when(breedImageService.getImageUrlsByBreedId("abc", 3))
                .thenReturn(CompletableFuture.completedFuture(List.of("img1", "img2")));

        List<String> images = listener.getImagesForRequest(request);

        assertEquals(2, images.size());
        assertEquals("img1", images.get(0));
    }

    @Test
    void testGetImagesForRequest_withoutBreedId_shouldReturnNull() {
        BreedRequestMessage request = new BreedRequestMessage(); // no breedId

        List<String> images = listener.getImagesForRequest(request);

        assertNull(images);
    }
}

