package com.itau.thecatapi.service;

import com.itau.thecatapi.dto.BreedImageResponseDTO;
import com.itau.thecatapi.model.BreedImage;
import com.itau.thecatapi.repository.BreedImageRepository;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BreedImageService {

    private static final Logger logger = LoggerFactory.getLogger(BreedImageService.class);

    @Autowired
    private BreedImageRepository breedImageRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Async
    public CompletableFuture<List<BreedImageResponseDTO>> getAllImages() {
        logger.info("Iniciando busca de todas as imagens - Thread: {}", Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando todas as imagens no repositório");
                List<BreedImage> images = breedImageRepository.findAll();
                logger.info("Encontradas {} imagens no total", images.size());

                List<BreedImageResponseDTO> result = images.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca de todas as imagens concluída em {} ms - Thread: {}",
                        duration, Thread.currentThread().getName());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar todas as imagens: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagens", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<BreedImageResponseDTO> getImageById(String id) {
        logger.info("Iniciando busca da imagem com ID: {} - Thread: {}", id, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando imagem com ID: {}", id);
                BreedImage image = breedImageRepository.findById(id).orElse(new BreedImage());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca da imagem com ID {} concluída em {} ms", id, duration);

                return convertToDTO(image);
            } catch (Exception e) {
                logger.error("Erro ao buscar imagem com ID {}: {}", id, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagem", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedImageResponseDTO>> getImagesByBreedId(String breedId) {
        logger.info("Iniciando busca de imagens para a raça com ID: {} - Thread: {}",
                breedId, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando imagens para a raça com ID: {}", breedId);
                List<BreedImage> images = breedImageRepository.findByBreedId(breedId);
                logger.info("Encontradas {} imagens para a raça com ID: {}", images.size(), breedId);

                List<BreedImageResponseDTO> result = images.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca de imagens para raça ID '{}' concluída em {} ms - {} resultados",
                        breedId, duration, result.size());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar imagens para a raça com ID {}: {}",
                        breedId, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagens por raça", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedImageResponseDTO>> getImagesByBreedId(String breedId, int limit) {
        logger.info("Iniciando busca de até {} imagens para a raça com ID: {} - Thread: {}",
                limit, breedId, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando até {} imagens para a raça com ID: {}", limit, breedId);
                List<BreedImage> images = breedImageRepository.findByBreedId(breedId);

                List<BreedImage> limitedImages = images.stream()
                        .limit(limit)
                        .collect(Collectors.toList());

                logger.info("Encontradas {} imagens (limitado a {}) para a raça com ID: {}",
                        limitedImages.size(), limit, breedId);

                List<BreedImageResponseDTO> result = limitedImages.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca de imagens para raça ID '{}' concluída em {} ms - {} resultados (limite: {})",
                        breedId, duration, result.size(), limit);

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar imagens para a raça com ID {}: {}",
                        breedId, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagens por raça", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<String>> getImageUrlsByBreedId(String breedId, int limit) {
        logger.info("Iniciando busca de até {} imagens para a raça com ID: {} - Thread: {}",
                limit, breedId, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando até {} imagens para a raça com ID: {}", limit, breedId);
                List<BreedImage> images = breedImageRepository.findByBreedId(breedId);

                List<BreedImage> limitedImages = images.stream()
                        .limit(limit)
                        .collect(Collectors.toList());

                logger.info("Encontradas {} imagens (limitado a {}) para a raça com ID: {}",
                        limitedImages.size(), limit, breedId);

                List<String> result = limitedImages.parallelStream()
                        .map(this::convertToDTO) // Converte para DTO primeiro
                        .map(BreedImageResponseDTO::getUrl) // Extrai a URL do DTO
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca de URLs para raça ID '{}' concluída em {} ms - {} resultados (limite: {})",
                        breedId, duration, result.size(), limit);

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar imagens para a raça com ID {}: {}",
                        breedId, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagens por raça", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedImageResponseDTO>> getImagesByCategoryId(Integer categoryId) {
        logger.info("Iniciando busca de imagens para a categoria com ID: {} - Thread: {}",
                categoryId, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando imagens para a categoria com ID: {}", categoryId);
                List<BreedImage> images = breedImageRepository.findByCategoryId(categoryId);
                logger.info("Encontradas {} imagens para a categoria com ID: {}", images.size(), categoryId);

                List<BreedImageResponseDTO> result = images.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca de imagens para categoria ID '{}' concluída em {} ms - {} resultados",
                        categoryId, duration, result.size());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar imagens para a categoria com ID {}: {}",
                        categoryId, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagens por categoria", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedImageResponseDTO>> getImagesByBreedIdAndCategoryId(String breedId, Integer categoryId) {
        logger.info("Iniciando busca de imagens para raça ID: '{}' e categoria ID: '{}' - Thread: {}",
                breedId, categoryId, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando imagens para raça ID: '{}' e categoria ID: '{}'", breedId, categoryId);
                List<BreedImage> images = breedImageRepository.findByBreedIdAndCategoryId(breedId, categoryId);
                logger.info("Encontradas {} imagens para raça ID: '{}' e categoria ID: '{}'",
                        images.size(), breedId, categoryId);

                List<BreedImageResponseDTO> result = images.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca por raça ID '{}' e categoria ID '{}' concluída em {} ms - {} resultados",
                        breedId, categoryId, duration, result.size());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar imagens para raça ID '{}' e categoria ID '{}': {}",
                        breedId, categoryId, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagens por raça e categoria", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedImageResponseDTO>> getFavoriteImages() {
        logger.info("Iniciando busca de imagens favoritas - Thread: {}", Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando imagens marcadas como favoritas");
                List<BreedImage> images = breedImageRepository.findByFavouriteTrue();
                logger.info("Encontradas {} imagens favoritas", images.size());

                List<BreedImageResponseDTO> result = images.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca de imagens favoritas concluída em {} ms - {} resultados",
                        duration, result.size());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar imagens favoritas: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar imagens favoritas", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<BreedImageResponseDTO> toggleFavorite(String imageId) {
        logger.info("Alternando status de favorito para imagem ID: {} - Thread: {}",
                imageId, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando imagem com ID: {} para alternar favorito", imageId);
                BreedImage image = breedImageRepository.findById(imageId)
                        .orElseThrow(() -> new RuntimeException("Imagem não encontrada com ID: " + imageId));

                image.setFavourite(!image.getFavourite());
                BreedImage updatedImage = breedImageRepository.save(image);

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Status de favorito alternado para imagem ID {} em {} ms. Novo status: {}",
                        imageId, duration, updatedImage.getFavourite());

                return convertToDTO(updatedImage);
            } catch (Exception e) {
                logger.error("Erro ao alternar favorito para imagem ID {}: {}", imageId, e.getMessage(), e);
                throw new RuntimeException("Falha ao alternar status de favorito", e);
            }
        }, executorService);
    }

    public BreedImageResponseDTO convertToDTO(BreedImage image) {
        logger.trace("Convertendo BreedImage para DTO: {}", image.getId());
        return new BreedImageResponseDTO(
                image.getId(),
                image.getUrl(),
                image.getWidth(),
                image.getHeight(),
                image.getFavourite(),
                image.getBreed() != null ? image.getBreed().getId() : null,
                image.getBreed() != null ? image.getBreed().getName() : null,
                image.getCategory() != null ? image.getCategory().getId() : null,
                image.getCategory() != null ? image.getCategory().getName() : null
        );
    }

    @PreDestroy
    public void shutdownExecutor() {
        logger.info("Encerrando ExecutorService do BreedImageService");
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                logger.warn("ExecutorService foi forçado a encerrar");
            }
            logger.info("ExecutorService encerrado com sucesso");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            logger.error("Interrupção durante encerramento do ExecutorService", e);
        }
    }
}