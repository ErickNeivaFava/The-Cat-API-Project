package com.itau.thecatapi.controller;

import com.itau.thecatapi.client.TheCatAPIClient;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.model.BreedImage;
import com.itau.thecatapi.model.Category;
import com.itau.thecatapi.service.DataCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/external")
public class ExternalApiController {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiController.class);

    private final TheCatAPIClient theCatAPIClient;
    private final DataCollectionService dataCollectionService;

    public ExternalApiController(TheCatAPIClient theCatAPIClient, DataCollectionService dataCollectionService) {
        this.theCatAPIClient = theCatAPIClient;
        this.dataCollectionService = dataCollectionService;
    }

    @GetMapping("/breeds")
    public CompletableFuture<ResponseEntity<List<Breed>>> getAllBreeds() {
        logger.info("Recebida requisição para listar todas as raças");
        return theCatAPIClient.getAllBreedsAsync()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    logger.error("Erro ao buscar raças", ex);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/breeds/images")
    public CompletableFuture<ResponseEntity<List<BreedImage>>> getAllBreedsImages() {
        logger.info("Recebida requisição para listar todas as raças com imagens");

        return theCatAPIClient.getAllBreedsAsync()
                .thenCompose(breeds -> theCatAPIClient.getBreedImagesAsync(breeds))
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    logger.error("Erro ao buscar imagens das raças", ex);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/breeds/categories")
    public CompletableFuture<ResponseEntity<List<Category>>> updateCategoryCache() {
        logger.info("Recebida requisição para listar categorias");
        return theCatAPIClient.getCategoriesAsync()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    logger.error("Erro ao buscar raças", ex);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/collect-data")
    public CompletableFuture<ResponseEntity<Void>> collectData() {
        logger.info("Recebida requisição para listar categorias");
        return dataCollectionService.collectAllData()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    logger.error("Erro ao coletar dados", ex);
                    return ResponseEntity.internalServerError().build();
                });
    }
}
