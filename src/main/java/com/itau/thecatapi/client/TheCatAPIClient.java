package com.itau.thecatapi.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.itau.thecatapi.model.Breed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TheCatAPIClient {

    @Value("${thecatapi.base-url}")
    private String baseUrl;

    @Value("${thecatapi.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final ExecutorService apiExecutor = Executors.newFixedThreadPool(10);

    public TheCatAPIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CompletableFuture<List<Breed>> getAllBreedsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Breed> allBreeds = new ArrayList<>();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String firstUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/breeds")
                    .queryParam("api_key", apiKey)
                    .queryParam("limit", 10)
                    .queryParam("page", 0)
                    .toUriString();

            try {
                ResponseEntity<String> firstResponse = restTemplate.exchange(
                        firstUrl, HttpMethod.GET, null, String.class);

                List<Breed> firstPageBreeds = objectMapper.readValue(
                        firstResponse.getBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Breed.class));

                if (firstPageBreeds != null) {
                    allBreeds.addAll(firstPageBreeds);

                    HttpHeaders headers = firstResponse.getHeaders();
                    int totalPages = 1;

                    if (headers.containsKey("Pagination-Count")) {
                        try {
                            int totalElements = Integer.parseInt(headers.getFirst("Pagination-Count"));
                            totalPages = (int) Math.ceil((double) totalElements / 10);
                        } catch (NumberFormatException e) {
                            return allBreeds;
                        }
                    }

                    for (int page = 1; page < totalPages; page++) {
                        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/breeds")
                                .queryParam("api_key", apiKey)
                                .queryParam("limit", 10)
                                .queryParam("page", page)
                                .toUriString();

                        ResponseEntity<String> response = restTemplate.exchange(
                                url, HttpMethod.GET, null, String.class);

                        List<Breed> currentPageBreeds = objectMapper.readValue(
                                response.getBody(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Breed.class));

                        if (currentPageBreeds != null && !currentPageBreeds.isEmpty()) {
                            allBreeds.addAll(currentPageBreeds);
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro ao mapear resposta JSON", e);
            }

            return allBreeds;
        }, apiExecutor);
    }

}
