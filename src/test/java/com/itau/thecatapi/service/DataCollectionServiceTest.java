package com.itau.thecatapi.service;

import com.itau.thecatapi.client.TheCatAPIClient;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.model.BreedImage;
import com.itau.thecatapi.model.Category;
import com.itau.thecatapi.repository.BreedImageRepository;
import com.itau.thecatapi.repository.BreedRepository;
import com.itau.thecatapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataCollectionServiceTest {

    @Mock
    private TheCatAPIClient catApiClient;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private BreedImageRepository breedImageRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private DataCollectionService dataCollectionService;

    @Captor
    private ArgumentCaptor<List<Breed>> breedCaptor;

    @Captor
    private ArgumentCaptor<List<Category>> categoryCaptor;

    @Captor
    private ArgumentCaptor<List<BreedImage>> breedImageCaptor;

    private List<Breed> testBreeds;
    private List<Category> testCategories;
    private List<BreedImage> testBreedImages;
    private List<BreedImage> testHatImages;
    private List<BreedImage> testSunglassesImages;

    @BeforeEach
    void setUp() {
        // Configurar dados de teste
        testBreeds = List.of(
                new Breed("beng", "Bengal", "Curious and energetic", "Thailand", "TH", "TH",
                        "Beautiful spotted cat", "12-15 years", 0, 1, "Leopard Cat",
                        5, 5, 4, 5, 5, 3, 3, 5, 4, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0,
                        "https://en.wikipedia.org/wiki/Bengal_cat", 0,
                        "http://cfa.org/Breeds/BreedsSthruT/Bengal.aspx",
                        "http://www.vetstreet.com/cats/bengal",
                        "https://vcahospitals.com/know-your-pet/cat-breeds/bengal",
                        "O3btzLlnp", new Breed.Weight("8-15 lbs", "4-7 kg")),


                new Breed("siam", "Siamese", "Vocal and social", "Thailand", "TH", "TH",
                        "Talkative and elegant cat", "15-20 years", 1, 1, "Meezer",
                        4, 5, 3, 3, 5, 1, 2, 5, 3, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0,
                        "https://en.wikipedia.org/wiki/Siamese_cat", 1,
                        "http://cfa.org/Breeds/BreedsSthruT/Siamese.aspx",
                        "http://www.vetstreet.com/cats/siamese",
                        "https://vcahospitals.com/know-your-pet/cat-breeds/siamese",
                        "ai5JRev4n", new Breed.Weight("8-10 lbs", "4-5 kg"))
        );

        testCategories = List.of(
                new Category(1, "hats"),
                new Category(2, "sunglasses")
        );

        // Criar instâncias de Breed para as imagens
        Breed bengBreed = testBreeds.get(0);
        Breed siamBreed = testBreeds.get(1);

        Category hatCategory = testCategories.get(0);
        Category sunglassesCategory = testCategories.get(1);

        testBreedImages = List.of(
                new BreedImage("O3btzLlnp", "https://example.com/image1.jpg", 800, 600, false, bengBreed, null),
                new BreedImage("ai5JRev4n", "https://example.com/image2.jpg", 1024, 768, false, siamBreed, null)
        );

        testHatImages = List.of(
                new BreedImage("hat1", "https://example.com/hat1.jpg", 800, 600, false, null, hatCategory),
                new BreedImage("hat2", "https://example.com/hat2.jpg", 1024, 768, false, null, hatCategory)
        );

        testSunglassesImages = List.of(
                new BreedImage("sung1", "https://example.com/sunglasses1.jpg", 800, 600, false, null, sunglassesCategory),
                new BreedImage("sung2", "https://example.com/sunglasses2.jpg", 1024, 768, false, null, sunglassesCategory)
        );
    }

    @Test
    void collectAllData_ShouldCollectAllDataSuccessfully() throws Exception {
        // Arrange
        when(catApiClient.getAllBreedsAsync())
                .thenReturn(CompletableFuture.completedFuture(testBreeds));
        when(catApiClient.getCategoriesAsync())
                .thenReturn(CompletableFuture.completedFuture(testCategories));
        when(catApiClient.getBreedImagesAsync(testBreeds))
                .thenReturn(CompletableFuture.completedFuture(testBreedImages));
        when(catApiClient.getBreedImagesByCriteriaAsync(List.of("hats")))
                .thenReturn(CompletableFuture.completedFuture(testHatImages));
        when(catApiClient.getBreedImagesByCriteriaAsync(List.of("sunglasses")))
                .thenReturn(CompletableFuture.completedFuture(testSunglassesImages));

        when(breedRepository.saveAll(anyList())).thenReturn(testBreeds);
        when(categoryRepository.saveAll(anyList())).thenReturn(testCategories);
        when(breedImageRepository.saveAll(anyList())).thenReturn(testBreedImages)
                .thenReturn(testHatImages)
                .thenReturn(testSunglassesImages);

        // Act
        CompletableFuture<Void> result = dataCollectionService.collectAllData();

        // Assert - Aguarda a conclusão do CompletableFuture
        assertDoesNotThrow(() -> result.get());

        // Verifica se todos os métodos foram chamados
        verify(catApiClient).getAllBreedsAsync();
        verify(catApiClient).getCategoriesAsync();
        verify(catApiClient).getBreedImagesAsync(testBreeds);
        verify(catApiClient).getBreedImagesByCriteriaAsync(List.of("hats"));
        verify(catApiClient).getBreedImagesByCriteriaAsync(List.of("sunglasses"));

        verify(breedRepository).saveAll(breedCaptor.capture());
        verify(categoryRepository).saveAll(categoryCaptor.capture());
        verify(breedImageRepository, times(3)).saveAll(breedImageCaptor.capture());

        // Verifica os dados salvos
        assertEquals(testBreeds, breedCaptor.getValue());
        assertEquals(testCategories, categoryCaptor.getValue());

        List<List<BreedImage>> allSavedImages = breedImageCaptor.getAllValues();
        assertTrue(allSavedImages.contains(testBreedImages));
        assertTrue(allSavedImages.contains(testHatImages));
        assertTrue(allSavedImages.contains(testSunglassesImages));
    }

    @Test
    void saveAllBreedsAsync_ShouldSaveBreedsSuccessfully() throws Exception {
        // Arrange
        when(catApiClient.getAllBreedsAsync())
                .thenReturn(CompletableFuture.completedFuture(testBreeds));
        when(breedRepository.saveAll(testBreeds)).thenReturn(testBreeds);

        // Act
        CompletableFuture<List<Breed>> result = dataCollectionService.saveAllBreedsAsync();

        // Assert
        List<Breed> savedBreeds = result.get();
        assertEquals(testBreeds, savedBreeds);
        verify(breedRepository).saveAll(testBreeds);
    }

    @Test
    void saveAllBreedsAsync_ShouldHandleException() throws Exception {
        // Arrange
        RuntimeException expectedException = new RuntimeException("API error");
        when(catApiClient.getAllBreedsAsync())
                .thenReturn(CompletableFuture.failedFuture(expectedException));

        // Act
        CompletableFuture<List<Breed>> result = dataCollectionService.saveAllBreedsAsync();

        // Assert
        List<Breed> savedBreeds = result.get();
        assertNull(savedBreeds);
        verify(breedRepository, never()).saveAll(anyList());
    }

    @Test
    void saveAllCategoriesAsync_ShouldSaveCategoriesSuccessfully() throws Exception {
        // Arrange
        when(catApiClient.getCategoriesAsync())
                .thenReturn(CompletableFuture.completedFuture(testCategories));
        when(categoryRepository.saveAll(testCategories)).thenReturn(testCategories);

        // Act
        CompletableFuture<List<Category>> result = dataCollectionService.saveAllCategoriesAsync();

        // Assert
        List<Category> savedCategories = result.get();
        assertEquals(testCategories, savedCategories);
        verify(categoryRepository).saveAll(testCategories);
    }

    @Test
    void saveAllBreedImagesAsync_ShouldSaveBreedImagesSuccessfully() throws Exception {
        // Arrange
        when(catApiClient.getBreedImagesAsync(testBreeds))
                .thenReturn(CompletableFuture.completedFuture(testBreedImages));
        when(breedImageRepository.saveAll(testBreedImages)).thenReturn(testBreedImages);

        // Act
        CompletableFuture<List<BreedImage>> result = dataCollectionService.saveAllBreedImagesAsync(testBreeds);

        // Assert
        List<BreedImage> savedImages = result.get();
        assertEquals(testBreedImages, savedImages);
        verify(breedImageRepository).saveAll(testBreedImages);
    }

    @Test
    void saveAllBreedImagesByCriteriaAsync_ShouldSaveImagesByCriteriaSuccessfully() throws Exception {
        // Arrange
        List<String> criteria = List.of("hats");
        when(catApiClient.getBreedImagesByCriteriaAsync(criteria))
                .thenReturn(CompletableFuture.completedFuture(testHatImages));
        when(breedImageRepository.saveAll(testHatImages)).thenReturn(testHatImages);

        // Act
        CompletableFuture<List<BreedImage>> result = dataCollectionService.saveAllBreedImagesByCriteriaAsync(criteria);

        // Assert
        List<BreedImage> savedImages = result.get();
        assertEquals(testHatImages, savedImages);
        verify(breedImageRepository).saveAll(testHatImages);
    }

    @Test
    void saveAllBreedImagesAsync_ShouldHandleRepositoryException() throws Exception {
        List<Breed> mockBreeds = List.of(new Breed());
        List<BreedImage> mockImages = List.of(new BreedImage());

        when(catApiClient.getBreedImagesAsync(mockBreeds)).thenReturn(CompletableFuture.completedFuture(mockImages));
        when(breedImageRepository.saveAll(mockImages)).thenThrow(new RuntimeException("DB error"));

        CompletableFuture<List<BreedImage>> result = dataCollectionService.saveAllBreedImagesAsync(mockBreeds);

        List<BreedImage> images = result.join();
        assertNull(images); // Espera null por causa do exceptionally
    }

//    @Test
//    void collectAllData_ShouldHandlePartialFailures() throws Exception {
//        // Arrange - Uma das chamadas falha
//        when(catApiClient.getAllBreedsAsync())
//                .thenReturn(CompletableFuture.completedFuture(testBreeds));
//        when(catApiClient.getCategoriesAsync())
//                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Categories API error")));
//        when(catApiClient.getBreedImagesByCriteriaAsync(anyList())) // Usando anyList() para qualquer lista
//                .thenReturn(CompletableFuture.completedFuture(testBreedImages));
//
//        when(breedRepository.saveAll(testBreeds)).thenReturn(testBreeds);
//        when(breedImageRepository.saveAll(testBreedImages)).thenReturn(testBreedImages);
//
//        // Act
//        CompletableFuture<Void> result = dataCollectionService.collectAllData();
//
//        // Assert - Ainda deve completar, mas com falha em uma parte
//        assertDoesNotThrow(() -> result.get());
//
//        // Verifica que as operações que não falharam foram executadas
//        verify(breedRepository).saveAll(testBreeds);
//        verify(breedImageRepository).saveAll(testBreedImages);
//    }
}
