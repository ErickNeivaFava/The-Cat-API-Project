package com.itau.thecatapi.controller;

import com.itau.thecatapi.dto.BreedResponseDTO;
import com.itau.thecatapi.exception.ResourceNotFoundException;
import com.itau.thecatapi.service.BreedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/breeds")
@Validated
public class BreedController {

    private static final Logger logger = LoggerFactory.getLogger(BreedController.class);

    @Autowired
    private BreedService breedService;

    @GetMapping
    public CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> getBreeds(
            @RequestParam(required = false) String temperament,
            @RequestParam(required = false) String origin) {

        logger.info("Iniciando consulta de raças - temperament: {}, origin: {}",
                temperament, origin);

        if (temperament != null && origin != null) {
            logger.debug("Buscando raças por temperament: {} e origin: {}", temperament, origin);
            return breedService.getBreedsByTemperamentAndOrigin(temperament, origin)
                    .thenCompose(breeds -> {
                        if (breeds == null || breeds.isEmpty()) {
                            logger.warn("Nenhuma raça encontrada para temperament: {} e origin: {}",
                                    temperament, origin);
                            return CompletableFuture.failedFuture(
                                    new ResourceNotFoundException("Nenhuma raça encontrada para o temperamento: " + temperament + " e origem: " + origin)
                            );
                        }
                        logger.info("Encontradas {} raças para temperament: {} e origin: {}",
                                breeds.size(), temperament, origin);
                        return CompletableFuture.completedFuture(ResponseEntity.ok(breeds));
                    })
                    .exceptionally(ex -> {
                        if (ex.getCause() instanceof ResourceNotFoundException) {
                            logger.warn("Recurso não encontrado: {}", ex.getCause().getMessage());
                            throw (ResourceNotFoundException) ex.getCause();
                        }
                        logger.error("Erro interno ao buscar raças por temperament e origin: {}",
                                ex.getMessage(), ex);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    });
        } else if (temperament != null) {
            // Apenas temperament fornecido
            logger.debug("Buscando raças por temperament: {}", temperament);
            return breedService.getBreedsByTemperament(temperament)
                    .thenCompose(breeds -> {
                        if (breeds == null || breeds.isEmpty()) {
                            logger.warn("Nenhuma raça encontrada para temperament: {}", temperament);
                            return CompletableFuture.failedFuture(
                                    new ResourceNotFoundException("Nenhuma raça encontrada para o temperamento: " + temperament)
                            );
                        }
                        logger.info("Encontradas {} raças para temperament: {}",
                                breeds.size(), temperament);
                        return CompletableFuture.completedFuture(ResponseEntity.ok(breeds));
                    })
                    .exceptionally(ex -> {
                        if (ex.getCause() instanceof ResourceNotFoundException) {
                            logger.warn("Recurso não encontrado: {}", ex.getCause().getMessage());
                            throw (ResourceNotFoundException) ex.getCause();
                        }
                        logger.error("Erro interno ao buscar raças por temperament: {}",
                                ex.getMessage(), ex);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    });
        } else if (origin != null) {
            // Apenas origin fornecido
            logger.debug("Buscando raças por origin: {}", origin);
            return breedService.getBreedsByOrigin(origin)
                    .thenCompose(breeds -> {
                        if (breeds == null || breeds.isEmpty()) {
                            logger.warn("Nenhuma raça encontrada para origin: {}", origin);
                            return CompletableFuture.failedFuture(
                                    new ResourceNotFoundException("Nenhuma raça encontrada para a origem: " + origin)
                            );
                        }
                        logger.info("Encontradas {} raças para origin: {}", breeds.size(), origin);
                        return CompletableFuture.completedFuture(ResponseEntity.ok(breeds));
                    })
                    .exceptionally(ex -> {
                        if (ex.getCause() instanceof ResourceNotFoundException) {
                            logger.error("Recurso não encontrado: {}", ex.getCause().getMessage());
                            throw (ResourceNotFoundException) ex.getCause();
                        }
                        logger.error("Erro interno ao buscar raças por origin: {}",
                                ex.getMessage(), ex);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    });
        } else {
            logger.debug("Buscando todas as raças");
            return breedService.getAllBreeds()
                    .thenCompose(breeds -> {
                        if (breeds == null || breeds.isEmpty()) {
                            logger.warn("Nenhuma raça encontrada no sistema");
                            return CompletableFuture.failedFuture(
                                    new ResourceNotFoundException("Nenhuma raça encontrada no sistema")
                            );
                        }
                        logger.info("Encontradas {} raças no total", breeds.size());
                        return CompletableFuture.completedFuture(ResponseEntity.ok(breeds));
                    })
                    .exceptionally(ex -> {
                        if (ex.getCause() instanceof ResourceNotFoundException) {
                            logger.warn("Recurso não encontrado: {}", ex.getCause().getMessage());
                            throw (ResourceNotFoundException) ex.getCause();
                        }
                        logger.error("Erro interno ao buscar todas as raças: {}",
                                ex.getMessage(), ex);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    });
        }
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<BreedResponseDTO>> getBreedById(@PathVariable String id) {
        logger.info("Iniciando consulta de raça por ID: {}", id);

        return breedService.getBreedById(id)
                .thenCompose(breed -> {
                    if (breed == null || breed.getId() == null) {
                        logger.warn("Raça não encontrada para o ID: {}", id);
                        return CompletableFuture.failedFuture(
                                new ResourceNotFoundException("Raça não encontrada no sistema para o ID: " + id)
                        );
                    }
                    logger.info("Raça encontrada com sucesso - ID: {}, Nome: {}",
                            breed.getId(), breed.getName());
                    return CompletableFuture.completedFuture(ResponseEntity.ok(breed));
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof ResourceNotFoundException) {
                        logger.error("Recurso não encontrado para ID {}: {}",
                                id, ex.getCause().getMessage());
                        throw (ResourceNotFoundException) ex.getCause();
                    }
                    logger.error("Erro interno ao buscar raça por ID {}: {}",
                            id, ex.getMessage(), ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}

