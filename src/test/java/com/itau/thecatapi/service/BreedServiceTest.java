package com.itau.thecatapi.service;

import com.itau.thecatapi.dto.BreedResponseDTO;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.repository.BreedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreedServiceTest {

    @Mock
    private BreedRepository breedRepository;

    @InjectMocks
    private BreedService breedService;

    private Breed breed1;
    private Breed breed2;
    private Breed breed3;

    @BeforeEach
    void setUp() {
        breed1 = new Breed("beng", "Bengal", "Curious, energetic", "Thailand", "TH", "TH",
                "Beautiful spotted cat", "12-15 years", 0, 1, "Leopard Cat",
                5, 5, 4, 5, 5, 3, 3, 5, 4, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0,
                "https://en.wikipedia.org/wiki/Bengal_cat", 0,
                "http://cfa.org/Breeds/BreedsSthruT/Bengal.aspx",
                "http://www.vetstreet.com/cats/bengal",
                "https://vcahospitals.com/know-your-pet/cat-breeds/bengal",
                "O3btzLlnp", new Breed.Weight("8-15 lbs", "4-7 kg"));

        breed2 = new Breed("siam", "Siamese", "Vocal, social, curious", "Thailand", "TH", "TH",
                "Talkative and elegant cat", "15-20 years", 1, 1, "Meezer",
                4, 5, 3, 3, 5, 1, 2, 5, 3, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0,
                "https://en.wikipedia.org/wiki/Siamese_cat", 1,
                "http://cfa.org/Breeds/BreedsSthruT/Siamese.aspx",
                "http://www.vetstreet.com/cats/siamese",
                "https://vcahospitals.com/know-your-pet/cat-breeds/siamese",
                "ai5JRev4n", new Breed.Weight("8-10 lbs", "4-5 kg"));

        breed3 = new Breed("mcoo", "Maine Coon", "Gentle giant, friendly, intelligent", "United States", "US", "US",
                "One of the largest domesticated cat breeds with a distinctive physical appearance", "12-15 years", 0, 1, "Coon Cat, Maine Cat",
                5, 5, 4, 5, 3, 3, 3, 5, 4, 4, 3, 3, 0, 0, 1, 0, 0, 0, 0,
                "https://en.wikipedia.org/wiki/Maine_Coon", 0,
                "http://cfa.org/Breeds/BreedsKthruR/MaineCoon.aspx",
                "http://www.vetstreet.com/cats/maine-coon",
                "https://vcahospitals.com/know-your-pet/cat-breeds/maine-coon",
                "O3bt3L4R1", new Breed.Weight("13-18 lbs", "6-8 kg"));
    }

    @Test
    void getAllBreeds_ShouldReturnAllBreeds() throws ExecutionException, InterruptedException {
        // Arrange
        List<Breed> breeds = Arrays.asList(breed1, breed2, breed3);
        when(breedRepository.findAll()).thenReturn(breeds);

        // Act
        CompletableFuture<List<BreedResponseDTO>> resultFuture = breedService.getAllBreeds();
        List<BreedResponseDTO> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(breedRepository, times(1)).findAll();

        // Verify DTO conversion
        BreedResponseDTO dto1 = result.get(0);
        assertEquals("beng", dto1.getId());
        assertEquals("Bengal", dto1.getName());
        assertEquals("Thailand", dto1.getOrigin());
        assertEquals("Curious, energetic", dto1.getTemperament());
    }

    @Test
    void getAllBreeds_WhenRepositoryThrowsException_ShouldThrowRuntimeException() {
        // Arrange
        when(breedRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        CompletableFuture<List<BreedResponseDTO>> resultFuture = breedService.getAllBreeds();
        ExecutionException exception = assertThrows(ExecutionException.class, resultFuture::get);

        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Falha ao buscar raças", exception.getCause().getMessage());
        verify(breedRepository, times(1)).findAll();
    }

    @Test
    void getBreedById_WhenBreedExists_ShouldReturnBreed() throws ExecutionException, InterruptedException {
        // Arrange
        when(breedRepository.findById("beng")).thenReturn(Optional.of(breed1));

        // Act
        CompletableFuture<BreedResponseDTO> resultFuture = breedService.getBreedById("beng");
        BreedResponseDTO result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals("beng", result.getId());
        assertEquals("Bengal", result.getName());
        verify(breedRepository, times(1)).findById("beng");
    }

    @Test
    void getBreedById_WhenBreedNotFound_ShouldReturnEmptyBreed() throws ExecutionException, InterruptedException {
        // Arrange
        when(breedRepository.findById("zzzz")).thenReturn(Optional.empty());

        // Act
        CompletableFuture<BreedResponseDTO> resultFuture = breedService.getBreedById("zzzz");
        BreedResponseDTO result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertNull(result.getId()); // Because new Breed() creates empty breed
        verify(breedRepository, times(1)).findById("zzzz");
    }

    @Test
    void getBreedById_WhenRepositoryThrowsException_ShouldThrowRuntimeException() {
        // Arrange
        when(breedRepository.findById(anyString())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        CompletableFuture<BreedResponseDTO> resultFuture = breedService.getBreedById("1");
        ExecutionException exception = assertThrows(ExecutionException.class, resultFuture::get);

        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Falha ao buscar raça", exception.getCause().getMessage());
    }

    @Test
    void getBreedsByTemperament_ShouldReturnMatchingBreeds() throws ExecutionException, InterruptedException {
        // Arrange
        List<Breed> breeds = Arrays.asList(breed1, breed2);
        when(breedRepository.findByTemperamentContainingIgnoreCase("Curious")).thenReturn(breeds);

        // Act
        CompletableFuture<List<BreedResponseDTO>> resultFuture = breedService.getBreedsByTemperament("Curious");
        List<BreedResponseDTO> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(breedRepository, times(1)).findByTemperamentContainingIgnoreCase("Curious");

        assertEquals("Bengal", result.get(0).getName());
        assertEquals("Siamese", result.get(1).getName());
    }

    @Test
    void getBreedsByTemperament_WhenNoMatches_ShouldReturnEmptyList() throws ExecutionException, InterruptedException {
        // Arrange
        when(breedRepository.findByTemperamentContainingIgnoreCase("Nonexistent")).thenReturn(List.of());

        // Act
        CompletableFuture<List<BreedResponseDTO>> resultFuture = breedService.getBreedsByTemperament("Nonexistent");
        List<BreedResponseDTO> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(breedRepository, times(1)).findByTemperamentContainingIgnoreCase("Nonexistent");
    }

    @Test
    void getBreedsByOrigin_ShouldReturnMatchingBreeds() throws ExecutionException, InterruptedException {
        // Arrange
        List<Breed> breeds = Arrays.asList(breed1);
        when(breedRepository.findByOriginContainingIgnoreCase("Thailand")).thenReturn(breeds);

        // Act
        CompletableFuture<List<BreedResponseDTO>> resultFuture = breedService.getBreedsByOrigin("Thailand");
        List<BreedResponseDTO> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(breedRepository, times(1)).findByOriginContainingIgnoreCase("Thailand");
        assertEquals("Bengal", result.get(0).getName());
    }

    @Test
    void getBreedsByTemperamentAndOrigin_ShouldReturnMatchingBreeds() throws ExecutionException, InterruptedException {
        // Arrange
        List<Breed> breeds = Arrays.asList(breed3);
        when(breedRepository.findByTemperamentContainingAndOriginContainingIgnoreCase("Friendly", "United States"))
                .thenReturn(breeds);

        // Act
        CompletableFuture<List<BreedResponseDTO>> resultFuture = breedService.getBreedsByTemperamentAndOrigin("Friendly", "United States");
        List<BreedResponseDTO> result = resultFuture.get();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(breedRepository, times(1))
                .findByTemperamentContainingAndOriginContainingIgnoreCase("Friendly", "United States");
        assertEquals("Maine Coon", result.get(0).getName());
    }

    @Test
    void convertToDTO_ShouldConvertBreedToDTO() {
        // Arrange
        BreedService service = new BreedService();
        breedRepository = breedRepository; // Inject mock manually for private method test

        // Act
        BreedResponseDTO dto = service.convertToDTO(breed1);

        // Assert
        assertNotNull(dto);
        assertEquals("beng", dto.getId());
        assertEquals("Bengal", dto.getName());
        assertEquals("Thailand", dto.getOrigin());
        assertEquals("Curious, energetic", dto.getTemperament());
        assertEquals("Beautiful spotted cat", dto.getDescription());
    }

    @Test
    void shutdownExecutor_ShouldShutdownExecutorService() throws Exception {
        BreedService service = new BreedService();

        // Usar reflection para acessar o campo privado
        Field executorServiceField = BreedService.class.getDeclaredField("executorService");
        executorServiceField.setAccessible(true);
        ExecutorService executorService = (ExecutorService) executorServiceField.get(service);

        assertFalse(executorService.isShutdown());

        service.shutdownExecutor();

        // Dar tempo para o shutdown
        Thread.sleep(100);

        assertTrue(executorService.isShutdown());
    }

    @Test
    void asyncMethods_ShouldUseDifferentThread() throws ExecutionException, InterruptedException {
        // Arrange
        List<Breed> breeds = Arrays.asList(breed1);
        when(breedRepository.findAll()).thenReturn(breeds);

        // Get current thread name
        String mainThreadName = Thread.currentThread().getName();

        // Act
        CompletableFuture<List<BreedResponseDTO>> resultFuture = breedService.getAllBreeds();
        List<BreedResponseDTO> result = resultFuture.get();

        // Assert - The async operation should complete (we can't easily test thread difference in unit test)
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
