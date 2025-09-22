package com.itau.thecatapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.thecatapi.dto.BreedImageDTO;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.model.BreedImage;
import com.itau.thecatapi.model.Category;
import com.itau.thecatapi.service.CategoryService;
import com.itau.thecatapi.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class TheCatAPIClient {

    private static final Logger logger = LoggerFactory.getLogger(TheCatAPIClient.class);

    private CategoryService categoryService;

    private final HttpUtils httpUtils;

    private final RestTemplate restTemplate;

    private final ExecutorService apiExecutor = Executors.newFixedThreadPool(10);

    public TheCatAPIClient(CategoryService categoryService, HttpUtils httpUtils, RestTemplate restTemplate) {
        this.categoryService = categoryService;
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
        logger.info("Iniciando obtenção de imagens para {} raças", breeds.size());

        List<String> breedIds = breeds.stream()
                .map(Breed::getId)
                .collect(Collectors.toList());

        ObjectMapper objectMapper = httpUtils.getObjectMapper();

        final AtomicInteger remainingRequests = new AtomicInteger(0);
        final AtomicLong resetTime = new AtomicLong(0);
        final Object lock = new Object();

        List<CompletableFuture<List<BreedImage>>> breedFutures = breedIds.stream()
                .map(breedId -> CompletableFuture.supplyAsync(() -> {
                    logger.debug("Processando imagens para breed ID: {}", breedId);

                    for (int attempt = 0; attempt < 3; attempt++) {
                        try {
                            synchronized (lock) {
                                long currentTime = System.currentTimeMillis();

                                if (currentTime > resetTime.get()) {
                                    logger.debug("Reset time atingido, reiniciando contador de requests");
                                    remainingRequests.set(0);
                                }

                                if (remainingRequests.get() >= 120) {
                                    long waitTime = resetTime.get() - currentTime;
                                    if (waitTime > 0) {
                                        logger.warn("Rate limit atingido ({} requests). Aguardando {} ms para reset",
                                                remainingRequests.get(), waitTime);
                                        Thread.sleep(waitTime);
                                        remainingRequests.set(0);
                                        logger.info("Rate limit resetado. Continuando processamento");
                                    }
                                }
                            }

                            String url = UriComponentsBuilder.fromHttpUrl(httpUtils.buildUrl("/images/search"))
                                    .queryParam("limit", 3)
                                    .queryParam("breed_ids", breedId)
                                    .toUriString();

                            logger.debug("Fazendo request para URL: {}", url);

                            ResponseEntity<String> response = restTemplate.exchange(
                                    url, HttpMethod.GET, httpUtils.createDefaultEntity(), String.class);

                            HttpHeaders headers = response.getHeaders();
                            if (headers.containsKey("ratelimit-remaining") &&
                                    headers.containsKey("ratelimit-reset")) {

                                synchronized (lock) {
                                    int newRemaining = Integer.parseInt(headers.getFirst("ratelimit-remaining"));
                                    remainingRequests.set(newRemaining);

                                    String resetTimeStr = headers.getFirst("ratelimit-reset");
                                    Instant resetInstant = Instant.parse(resetTimeStr);
                                    resetTime.set(resetInstant.toEpochMilli());

                                    logger.debug("Rate limit atualizado - Restantes: {}, Reset em: {}",
                                            newRemaining, resetInstant);
                                }
                            }

                            List<BreedImageDTO> responses = objectMapper.readValue(
                                    response.getBody(),
                                    objectMapper.getTypeFactory().constructCollectionType(List.class, BreedImageDTO.class));

                            logger.info("Sucesso ao obter {} imagens para breed {}", responses.size(), breedId);

                            return responses.stream()
                                    .map(BreedImage::fromResponse)
                                    .collect(Collectors.toList());

                        } catch (Exception e) {
                            logger.warn("Tentativa {} falhou para breed {}: {}",
                                    (attempt + 1), breedId, e.getMessage(), e);

                            if (e.getMessage().contains("429") || e.getMessage().contains("Too Many Requests")) {
                                logger.error("Rate limiting detectado (429). Aguardando 60 segundos");
                                try {
                                    Thread.sleep(60000);
                                } catch (InterruptedException ie) {
                                    logger.error("Thread interrompida durante espera de rate limit", ie);
                                    Thread.currentThread().interrupt();
                                }
                            }

                            if (attempt == 2) {
                                logger.error("Todas as 3 tentativas falharam para breed: {}", breedId);
                                return Collections.<BreedImage>emptyList();
                            }

                            long backoffTime = (long) (Math.pow(2, attempt) * 1000);
                            logger.debug("Aplicando backoff de {} ms antes da próxima tentativa", backoffTime);

                            try {
                                Thread.sleep(backoffTime);
                            } catch (InterruptedException ie) {
                                logger.error("Thread interrompida durante backoff", ie);
                                Thread.currentThread().interrupt();
                                return Collections.<BreedImage>emptyList();
                            }
                        }
                    }
                    return Collections.<BreedImage>emptyList();
                }, apiExecutor))
                .collect(Collectors.toList());

        logger.info("Todas as requisições assíncronas iniciadas. Aguardando conclusão...");

        return CompletableFuture.allOf(breedFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<BreedImage> result = breedFutures.stream()
                            .map(CompletableFuture::join)
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                    logger.info("Processamento concluído. Total de {} imagens obtidas", result.size());
                    return result;
                });
    }

    public CompletableFuture<List<Category>> getCategoriesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Category> categories = new ArrayList<>();

            ObjectMapper objectMapper = httpUtils.getObjectMapper();

            //category 1 = hats, 4 = sunglasses
            try {


                String url = UriComponentsBuilder.fromHttpUrl(httpUtils.buildUrl("/categories"))
                        .toUriString();

                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.GET, httpUtils.createDefaultEntity(), String.class);

                categories = objectMapper.readValue(
                        response.getBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Category.class));

            } catch (Exception e) {
                throw new RuntimeException("Erro ao mapear resposta JSON", e);
            }

            return categories;
        }, apiExecutor);
    }

    public CompletableFuture<List<BreedImage>> getBreedImagesByCriteriaAsync(List<String> criteria) {
        return CompletableFuture.supplyAsync(() -> {
            List<BreedImage> breedImages = new ArrayList<>();

            ObjectMapper objectMapper = httpUtils.getObjectMapper();

            List<String> categoryIds = categoryService.getCategoriesByNames(criteria).stream()
                    .map(category -> String.valueOf(category.getId()))
                    .collect(Collectors.toList());;

            //category 1 = hats, 4 = sunglasses
            try {
                String categoryIdsParam = String.join(",", categoryIds);

                String url = UriComponentsBuilder.fromHttpUrl(httpUtils.buildUrl("/images/search"))
                        .queryParam("limit", 3)
                        .queryParam("category_ids", categoryIdsParam)
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


            } catch (Exception e) {
                throw new RuntimeException("Erro ao mapear resposta JSON", e);
            }

            return breedImages;
        }, apiExecutor);

    }

}
