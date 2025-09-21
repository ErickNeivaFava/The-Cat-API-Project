package com.itau.thecatapi.service;


import com.itau.thecatapi.client.TheCatAPIClient;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.model.BreedImage;
import com.itau.thecatapi.model.Category;
import com.itau.thecatapi.repository.BreedImageRepository;
import com.itau.thecatapi.repository.BreedRepository;
import com.itau.thecatapi.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class DataCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(DataCollectionService.class);

    private final TheCatAPIClient catApiClient;
    private final CategoryRepository categoryRepository;
    private final BreedRepository breedRepository;
    private final BreedImageRepository breedImageRepository;

    public DataCollectionService(TheCatAPIClient catApiClient,
                                 CategoryRepository categoryRepository,
                                 BreedRepository breedRepository,
                                 BreedImageRepository breedImageRepository) {
        this.catApiClient = catApiClient;
        this.categoryRepository = categoryRepository;
        this.breedRepository = breedRepository;
        this.breedImageRepository = breedImageRepository;
    }

    @Async
    public CompletableFuture<Void> collectAllData() {
        logger.info("Iniciando coleta de dados da The Cat API");

        logger.info("Executando em paralelo a coleta de raças e categorias");
        CompletableFuture<List<Breed>> futureBreed = saveAllBreedsAsync();
        CompletableFuture<List<Category>> futureCategory = saveAllCategoriesAsync();

        CompletableFuture<List<BreedImage>> futureHats = futureCategory.thenComposeAsync(categories -> {
            logger.info("Coleta de categorias concluída, iniciando coleta de gatos com chapéu");
            return saveAllBreedImagesByCriteriaAsync(List.of("hats"));
        });

        CompletableFuture<List<BreedImage>> futureSunglasses = futureCategory.thenComposeAsync(categories -> {
            logger.info("Coleta de categorias concluída, iniciando coleta de gatos com óculos");
            return saveAllBreedImagesByCriteriaAsync(List.of("sunglasses"));
        });

        CompletableFuture<List<BreedImage>> futureBreedImage = futureBreed.thenComposeAsync(breeds -> {
            logger.info("Coleta de raças concluída, executando coleta de imagens de raça");
            return saveAllBreedImagesAsync(breeds);
        });

        return CompletableFuture.allOf(futureHats, futureSunglasses, futureBreedImage)
                .thenApply(ignored -> {
                    logger.info("Processo de coleta de dados concluído!");
                    return null;
                });
    }

    private CompletableFuture<List<Breed>> saveAllBreedsAsync() {
        logger.info("Coletando informações das raças de forma assíncrona");

        return catApiClient.getAllBreedsAsync()
                .thenCompose(breeds -> {
                    logger.debug("Received {} breeds, saving in batch", breeds.size());

                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            List<Breed> savedBreeds = breedRepository.saveAll(breeds);
                            logger.info("Successfully saved {} breeds", savedBreeds.size());
                            return savedBreeds;
                        } catch (Exception e) {
                            logger.error("Error saving breeds batch", e);
                            throw new CompletionException(e);
                        }
                    });
                })
                .exceptionally(ex -> {
                    logger.error("Failed to collect breeds", ex);
                    return null;
                });
    }

    private CompletableFuture<List<BreedImage>> saveAllBreedImagesAsync(List<Breed> breeds) {
        logger.info("Coletando informações das imagens de raças de forma assíncrona");

        return catApiClient.getBreedImagesAsync(breeds)
                .thenCompose(breedImages -> {
                    logger.debug("Received {} breed images, saving in batch", breedImages.size());

                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            List<BreedImage> savedBreedImages = breedImageRepository.saveAll(breedImages);
                            logger.info("Successfully saved {} breed images", savedBreedImages.size());
                            return savedBreedImages;
                        } catch (Exception e) {
                            logger.error("Error saving breed images batch", e);
                            throw new CompletionException(e);
                        }
                    });
                })
                .exceptionally(ex -> {
                    logger.error("Failed to collect breed images", ex);
                    return null;
                });
    }

    private CompletableFuture<List<BreedImage>> saveAllBreedImagesByCriteriaAsync(List<String> categories) {
        logger.info("Coletando informações das imagens específicas ({}) de forma assíncrona", categories);

        return catApiClient.getBreedImagesByCriteriaAsync(categories)
                .thenCompose(breedImages -> {
                    logger.debug("Received {} breed images, saving in batch", breedImages.size());

                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            List<BreedImage> savedBreedImages = breedImageRepository.saveAll(breedImages);
                            logger.info("Successfully saved {} specific ({}) breed images", savedBreedImages.size(), categories);
                            return savedBreedImages;
                        } catch (Exception e) {
                            logger.error("Error saving breed images batch", e);
                            throw new CompletionException(e);
                        }
                    });
                })
                .exceptionally(ex -> {
                    logger.error("Failed to collect breed images", ex);
                    return null;
                });
    }

    private CompletableFuture<List<Category>> saveAllCategoriesAsync() {
        logger.info("Coletando informações das categorias de forma assíncrona");
        return catApiClient.getCategoriesAsync().thenCompose(categories -> {
            logger.debug("Received {} categories, saving in batch", categories.size());
            return CompletableFuture.supplyAsync(() -> {
                try {
                    List<Category> savedCategories = categoryRepository.saveAll(categories);
                    logger.info("Successfully saved {} categories", savedCategories.size());
                    return savedCategories;
                } catch (Exception e) {
                    logger.error("Error saving categories batch", e);
                    throw new CompletionException(e);
                }
            });
        }).exceptionally(ex -> {
            logger.error("Failed to collect categories", ex);
            return null;
        });
    }

//    private CompletableFuture<List<Breed>> saveBreedsIndividuallyAsync() {
//        logger.info("Coletando informações das raças forma assíncrona");
//
//        CompletableFuture<List<Breed>> breedsFuture = catApiClient.getAllBreedsAsync();
//        ExecutorService executor = Executors.newFixedThreadPool(10); // Limite de threads
//
//        return breedsFuture.thenComposeAsync(breeds -> {
//            List<CompletableFuture<Breed>> futures = breeds.stream()
//                    .map(breed -> CompletableFuture.supplyAsync(() -> {
//                        try {
//                            Breed savedBreed = breedRepository.save(breed);
//                            logger.debug("Raça salva: {}", breed.getName());
//                            return savedBreed;
//                        } catch (Exception e) {
//                            logger.error("Erro ao salvar raça: {}", breed.getName(), e);
//                            throw new RuntimeException("Erro ao salvar raça: " + breed.getName(), e);
//                        }
//                    }, executor))
//                    .collect(Collectors.toList());
//
//            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                    .thenApply(v -> futures.stream()
//                            .map(CompletableFuture::join)
//                            .collect(Collectors.toList()));
//        }, executor).whenComplete((result, ex) -> {
//            executor.shutdown();
//            if (ex != null) {
//                logger.error("Erro ao processar raças", ex);
//            } else {
//                logger.info("Todas as raças foram processadas com sucesso");
//            }
//        });
//    }
//
//    private CompletableFuture<List<BreedImage>> saveBreedImagesIndividuallyAsync(List<Breed> breeds) {
//        logger.info("Coletando informações das imagens de raças forma assíncrona");
//
//        CompletableFuture<List<BreedImage>> breedImagesFuture = catApiClient.getBreedImagesAsync(breeds);
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//
//        return breedImagesFuture.thenComposeAsync(breedImages -> {
//            List<CompletableFuture<BreedImage>> futures = breedImages.stream()
//                    .map(breedImage -> CompletableFuture.supplyAsync(() -> {
//                        try {
//                            BreedImage savedBreedImage = breedImageRepository.save(breedImage);
//                            logger.debug("Imagem de raça salva: {}", breedImage.getId());
//                            return savedBreedImage;
//                        } catch (Exception e) {
//                            logger.error("Erro ao salvar imagem de raça: {}", breedImage.getId(), e);
//                            throw new RuntimeException("Erro ao salvar imagem de raça: " + breedImage.getId(), e);
//                        }
//                    }, executor))
//                    .collect(Collectors.toList());
//
//            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                    .thenApply(v -> futures.stream()
//                            .map(CompletableFuture::join)
//                            .collect(Collectors.toList()));
//        }, executor).whenComplete((result, ex) -> {
//            executor.shutdown();
//            if (ex != null) {
//                logger.error("Erro ao processar imagens de raças", ex);
//            } else {
//                logger.info("Todas as imagens de raças foram processadas com sucesso");
//            }
//        });
//    }
//
//    private CompletableFuture<List<BreedImage>> saveBreedImagesByCriteriaIndividuallyAsync(List<String> categories) {
//        logger.info("Coletando informações das imagens específicas ({}) de forma assíncrona", categories);
//
//        CompletableFuture<List<BreedImage>> breedImagesFuture = catApiClient.getBreedImagesByCriteriaAsync(categories);
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//
//        return breedImagesFuture.thenComposeAsync(breedImages -> {
//            List<CompletableFuture<BreedImage>> futures = breedImages.stream()
//                    .map(breedImage -> CompletableFuture.supplyAsync(() -> {
//                        try {
//                            BreedImage savedBreedImage = breedImageRepository.save(breedImage);
//                            logger.debug("Imagem de raça salva: {}", breedImage.getId());
//                            return savedBreedImage;
//                        } catch (Exception e) {
//                            logger.error("Erro ao salvar imagem de raça: {}", breedImage.getId(), e);
//                            throw new RuntimeException("Erro ao salvar imagem de raça: " + breedImage.getId(), e);
//                        }
//                    }, executor))
//                    .collect(Collectors.toList());
//
//            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                    .thenApply(v -> futures.stream()
//                            .map(CompletableFuture::join)
//                            .collect(Collectors.toList()));
//        }, executor).whenComplete((result, ex) -> {
//            executor.shutdown();
//            if (ex != null) {
//                logger.error("Erro ao processar imagens de raças", ex);
//            } else {
//                logger.info("Todas as imagens de raças foram processadas com sucesso");
//            }
//        });
//    }
//
//    private CompletableFuture<List<Category>> saveCategoriesIndividuallyAsync() {
//        logger.info("Coletando informações das categorias de forma assincrona");
//
//        CompletableFuture<List<Category>> categoriesFuture = catApiClient.getCategoriesAsync();
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//
//        return categoriesFuture.thenComposeAsync(categories -> {
//            List<CompletableFuture<Category>> futures = categories.stream()
//                    .map(category -> CompletableFuture.supplyAsync(() -> {
//                        try {
//                            Category savedCategory = categoryRepository.save(category);
//                            logger.debug("Categoria salva: {}", category.getName());
//                            return savedCategory;
//                        } catch (Exception e) {
//                            logger.error("Erro ao salvar categoria: {}", category.getName(), e);
//                            throw new RuntimeException("Erro ao salvar categoria: " + category.getName(), e);
//                        }
//                    }, executor))
//                    .collect(Collectors.toList());
//
//            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                    .thenApply(v -> futures.stream()
//                            .map(CompletableFuture::join)
//                            .collect(Collectors.toList()));
//        }, executor).whenComplete((result, ex) -> {
//            executor.shutdown();
//            if (ex != null) {
//                logger.error("Erro ao processar categorias", ex);
//            } else {
//                logger.info("Todas as categorias foram processadas com sucesso");
//            }
//        });
//    }

}
