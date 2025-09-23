package com.itau.thecatapi.service;

import com.itau.thecatapi.dto.BreedResponseDTO;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.repository.BreedRepository;
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
public class BreedService {

    private static final Logger logger = LoggerFactory.getLogger(BreedService.class);

    @Autowired
    private BreedRepository breedRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Async
    public CompletableFuture<List<BreedResponseDTO>> getAllBreeds() {
        logger.info("Iniciando busca de todas as raças - Thread: {}", Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando todas as raças no repositório");
                List<Breed> breeds = breedRepository.findAll();
                logger.info("Encontradas {} raças no total", breeds.size());

                List<BreedResponseDTO> result = breeds.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca de todas as raças concluída em {} ms - Thread: {}",
                        duration, Thread.currentThread().getName());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar todas as raças: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar raças", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<BreedResponseDTO> getBreedById(String id) {
        logger.info("Iniciando busca da raça com ID: {} - Thread: {}", id, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando raça com ID: {}", id);
                Breed breed = breedRepository.findById(id).orElse(new Breed());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca da raça com ID {} concluída em {} ms", id, duration);

                return convertToDTO(breed);
            } catch (Exception e) {
                logger.error("Erro ao buscar raça com ID {}: {}", id, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar raça", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedResponseDTO>> getBreedsByTemperament(String temperament) {
        logger.info("Iniciando busca por raças com temperamento: '{}' - Thread: {}",
                temperament, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando raças com temperamento contendo: '{}'", temperament);
                List<Breed> breeds = breedRepository.findByTemperamentContainingIgnoreCase(temperament);
                logger.info("Encontradas {} raças com temperamento: '{}'", breeds.size(), temperament);

                List<BreedResponseDTO> result = breeds.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca por temperamento '{}' concluída em {} ms - {} resultados",
                        temperament, duration, result.size());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar raças por temperamento '{}': {}",
                        temperament, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar raças por temperamento", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedResponseDTO>> getBreedsByOrigin(String origin) {
        logger.info("Iniciando busca por raças com origem: '{}' - Thread: {}",
                origin, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando raças com origem contendo: '{}'", origin);
                List<Breed> breeds = breedRepository.findByOriginContainingIgnoreCase(origin);
                logger.info("Encontradas {} raças com origem: '{}'", breeds.size(), origin);

                List<BreedResponseDTO> result = breeds.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca por origem '{}' concluída em {} ms - {} resultados",
                        origin, duration, result.size());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar raças por origem '{}': {}",
                        origin, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar raças por origem", e);
            }
        }, executorService);
    }

    @Async
    public CompletableFuture<List<BreedResponseDTO>> getBreedsByTemperamentAndOrigin(String temperament, String origin) {
        logger.info("Iniciando busca por raças com temperamento: '{}' e origem: '{}' - Thread: {}",
                temperament, origin, Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Buscando raças com temperamento contendo: '{}' e origem contendo: '{}'",
                        temperament, origin);
                List<Breed> breeds = breedRepository.findByTemperamentContainingAndOriginContainingIgnoreCase(temperament, origin);
                logger.info("Encontradas {} raças com temperamento: '{}' e origem: '{}'",
                        breeds.size(), temperament, origin);

                List<BreedResponseDTO> result = breeds.parallelStream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Busca por temperamento '{}' e origem '{}' concluída em {} ms - {} resultados",
                        temperament, origin, duration, result.size());

                return result;
            } catch (Exception e) {
                logger.error("Erro ao buscar raças por temperamento '{}' e origem '{}': {}",
                        temperament, origin, e.getMessage(), e);
                throw new RuntimeException("Falha ao buscar raças por temperamento e origem", e);
            }
        }, executorService);
    }

    public BreedResponseDTO convertToDTO(Breed breed) {
        logger.trace("Convertendo Breed para DTO: {}", breed.getName());
        return new BreedResponseDTO(
                breed.getId(),
                breed.getName(),
                breed.getOrigin(),
                breed.getTemperament(),
                breed.getDescription(),
                "https://cdn2.thecatapi.com/images/" + breed.getReferenceImageId() + ".jpg"
        );
    }

    @PreDestroy
    public void shutdownExecutor() {
        logger.info("Encerrando ExecutorService do BreedService");
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
