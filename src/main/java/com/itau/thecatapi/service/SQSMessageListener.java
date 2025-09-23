package com.itau.thecatapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.thecatapi.dto.BreedResponseDTO;
import com.itau.thecatapi.message.BreedRequestMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SQSMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SQSMessageListener.class);

    @Autowired
    private BreedService breedService;

    @Autowired
    private BreedImageService breedImageService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @SqsListener(value = "${spring.cloud.aws.sqs.queue.url}")
    public void receiveMessage(String message) {
        try {
            logger.info("Mensagem recebida do SQS: {}", message);

            // Converter JSON para objeto
            BreedRequestMessage breedRequest = objectMapper.readValue(message, BreedRequestMessage.class);

            processBreedRequest(breedRequest);

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem do SQS: {}", e.getMessage(), e);
            throw new RuntimeException("Falha no processamento da mensagem SQS", e);
        }
    }

    @Async
    public void processBreedRequest(BreedRequestMessage request) {
        logger.info("Processando requisição assíncrona. MessageID: {}, Email: {}",
                request.getMessageId(), request.getEmail());

        try {
            String breedInfo = processBreedInformation(request);
            List<String> imageUrls = getImagesForRequest(request);

            // Enviar email
            emailService.sendBreedInfoEmail(
                    request.getEmail(),
                    "🐱 Informações sobre Raças de Gatos",
                    breedInfo,
                    imageUrls
            );

            logger.info("Requisição processada com sucesso. MessageID: {}", request.getMessageId());

        } catch (Exception e) {
            logger.error("Erro ao processar requisição MessageID {}: {}",
                    request.getMessageId(), e.getMessage(), e);
        }
    }

    public String processBreedInformation(BreedRequestMessage request) {
        StringBuilder info = new StringBuilder();

        if (request.getBreedId() != null) {
            BreedResponseDTO breed = breedService.getBreedById(request.getBreedId()).join();
            info.append(String.format("""
                Raça: %s
                Origem: %s
                Temperamento: %s
                Descrição: %s
                """, breed.getName(), breed.getOrigin(), breed.getTemperament(), breed.getDescription()));

        } else if (request.getTemperament() != null) {
            List<BreedResponseDTO> breeds = breedService.getBreedsByTemperament(request.getTemperament()).join();
            info.append(String.format("Raças com temperamento '%s':\\n\\n", request.getTemperament()));
            breeds.forEach(breed -> info.append(String.format(
                    "- %s (Origem: %s)\\n", breed.getName(), breed.getOrigin())));

        } else if (request.getOrigin() != null) {
            List<BreedResponseDTO> breeds = breedService.getBreedsByOrigin(request.getOrigin()).join();
            info.append(String.format("Raças da origem '%s':\\n\\n", request.getOrigin()));
            breeds.forEach(breed -> info.append(String.format(
                    "- %s (Temperamento: %s)\\n", breed.getName(), breed.getTemperament())));

        } else {
            List<BreedResponseDTO> breeds = breedService.getAllBreeds().join();
            info.append("Todas as raças de gatos:\\n\\n");
            breeds.forEach(breed -> info.append(String.format(
                    "- %s (Origem: %s, Temperamento: %s)\\n",
                    breed.getName(), breed.getOrigin(), breed.getTemperament())));
        }

        return info.toString();
    }

    public List<String> getImagesForRequest(BreedRequestMessage request) {
        if (request.getBreedId() != null) {
            return breedImageService.getImageUrlsByBreedId(request.getBreedId(), 3).join();
        }
        return null;
    }
}