package com.itau.thecatapi.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.itau.thecatapi.dto.BreedImageDTO;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.model.BreedImage;
import com.itau.thecatapi.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class TheCatAPIClient {

//    @Value("${thecatapi.base-url}")
//    private String baseUrl;
//
//    @Value("${thecatapi.api-key}")
//    private String apiKey;

    private final HttpUtils httpUtils;

    private final RestTemplate restTemplate;

    private final ExecutorService apiExecutor = Executors.newFixedThreadPool(10);

    public TheCatAPIClient(HttpUtils httpUtils, RestTemplate restTemplate) {
        this.httpUtils = httpUtils;
        this.restTemplate = restTemplate;
    }


    public CompletableFuture<List<Breed>> getAllBreedsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Breed> allBreeds = new ArrayList<>();

            ObjectMapper objectMapper = httpUtils.getObjectMapper();

            String firstUrl = UriComponentsBuilder.fromHttpUrl(httpUtils.buildUrl("/breeds"))
                    .queryParam("limit", 10)
                    .queryParam("page", 0)
                    .toUriString();

            try {
                ResponseEntity<String> firstResponse = restTemplate.exchange(
                        firstUrl, HttpMethod.GET, httpUtils.createDefaultEntity(), String.class);

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
                        String url = UriComponentsBuilder.fromHttpUrl(httpUtils.buildUrl("/breeds"))
                                .queryParam("limit", 10)
                                .queryParam("page", page)
                                .toUriString();

                        ResponseEntity<String> response = restTemplate.exchange(
                                url, HttpMethod.GET, httpUtils.createDefaultEntity(), String.class);

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

    public CompletableFuture<List<BreedImage>> getBreedImagesAsync(List<Breed> breeds) {
        return CompletableFuture.supplyAsync(() -> {
            List<BreedImage> breedImages = new ArrayList<>();

            List<String> breedIds = breeds.stream()
                    .map(Breed::getId)
                    .collect(Collectors.toList());

            List<List<String>> batches = new ArrayList<>();

            for (int i = 0; i < breedIds.size(); i += 10) {
                int end = Math.min(breedIds.size(), i + 10);
                batches.add(breedIds.subList(i, end));
            }

            ObjectMapper objectMapper = httpUtils.getObjectMapper();

            for (List<String> batch : batches) {
                try {
                    String breedIdsParam = String.join(",", batch);

                    String url = UriComponentsBuilder.fromHttpUrl(httpUtils.buildUrl("/images/search"))
                            .queryParam("limit", 3)
                            .queryParam("breed_ids", breedIdsParam)
                            .toUriString();

                    ResponseEntity<String> response = restTemplate.exchange(
                            url, HttpMethod.GET, httpUtils.createDefaultEntity(), String.class);

                    List<BreedImageDTO> responses = objectMapper.readValue(
                            response.getBody(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, BreedImageDTO.class));

                    List<BreedImage> batchImages = responses.stream()
                            .map(BreedImage::fromResponse)
                            .collect(Collectors.toList());

                    if (batchImages != null && !batchImages.isEmpty()) {
                        breedImages.addAll(batchImages);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }

                } catch (Exception e) {
                    System.err.println("Erro ao processar batch: " + batch + " - " + e.getMessage());
                }
            }
            return breedImages;
        }, apiExecutor);

    }

}
