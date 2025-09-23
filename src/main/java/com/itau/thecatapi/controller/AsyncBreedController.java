package com.itau.thecatapi.controller;

import com.itau.thecatapi.message.BreedRequestMessage;
import com.itau.thecatapi.service.SQSMessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/async/breeds")
public class AsyncBreedController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncBreedController.class);

    @Autowired
    private SQSMessagingService sqsMessagingService;

    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllBreedsAsync(@RequestParam String email) {
        logger.info("Recebida requisição assíncrona para listar todas as raças. Email: {}", email);

        BreedRequestMessage request = new BreedRequestMessage(email, null, null, null);
        sqsMessagingService.sendBreedRequest(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Requisição recebida com sucesso");
        response.put("status", "processing");
        response.put("email", email);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/breed/{breedId}")
    public ResponseEntity<Map<String, Object>> getBreedByIdAsync(
            @PathVariable String breedId,
            @RequestParam String email) {

        logger.info("Recebida requisição assíncrona para raça: {}. Email: {}", breedId, email);

        BreedRequestMessage request = new BreedRequestMessage(email, breedId, null, null);
        sqsMessagingService.sendBreedRequest(request);

        Map<String, Object> response = buildResponse("Informações da raça serão enviadas por email", email);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/temperament")
    public ResponseEntity<Map<String, Object>> getBreedsByTemperamentAsync(
            @RequestParam String temperament,
            @RequestParam String email) {

        logger.info("Recebida requisição assíncrona por temperamento: {}. Email: {}", temperament, email);

        BreedRequestMessage request = new BreedRequestMessage(email, null, null, temperament);
        sqsMessagingService.sendBreedRequest(request);

        Map<String, Object> response = buildResponse("Raças por temperamento serão enviadas por email", email);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/origin")
    public ResponseEntity<Map<String, Object>> getBreedsByOriginAsync(
            @RequestParam String origin,
            @RequestParam String email) {

        logger.info("Recebida requisição assíncrona por origem: {}. Email: {}", origin, email);

        BreedRequestMessage request = new BreedRequestMessage(email, null, origin, null);
        sqsMessagingService.sendBreedRequest(request);

        Map<String, Object> response = buildResponse("Raças por origem serão enviadas por email", email);
        return ResponseEntity.accepted().body(response);
    }

    private Map<String, Object> buildResponse(String message, String email) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "processing");
        response.put("email", email);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
