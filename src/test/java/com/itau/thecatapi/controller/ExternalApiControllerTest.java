package com.itau.thecatapi.controller;

import com.itau.thecatapi.client.TheCatAPIClient;
import com.itau.thecatapi.model.Breed;
import com.itau.thecatapi.model.BreedImage;
import com.itau.thecatapi.model.Category;
import com.itau.thecatapi.service.DataCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
class ExternalApiControllerTest {

    @Mock
    private TheCatAPIClient theCatAPIClient;

    @Mock
    private DataCollectionService dataCollectionService;

    @Mock
    private Logger logger;

    @InjectMocks
    private ExternalApiController externalApiController;

    private Breed breed1;
    private Breed breed2;
    private Breed breed3;
    private Breed breed4;
    private BreedImage breedImage1;
    private BreedImage breedImage2;
    private BreedImage breedImage3;
    private BreedImage breedImage4;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        breed1 = new Breed("beng", "Bengal", "Curious and energetic", "Thailand", "TH", "TH",
                "Beautiful spotted cat", "12-15 years", 0, 1, "Leopard Cat",
                5, 5, 4, 5, 5, 3, 3, 5, 4, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0,
                "https://en.wikipedia.org/wiki/Bengal_cat", 0,
                "http://cfa.org/Breeds/BreedsSthruT/Bengal.aspx",
                "http://www.vetstreet.com/cats/bengal",
                "https://vcahospitals.com/know-your-pet/cat-breeds/bengal",
                "O3btzLlnp", new Breed.Weight("8-15 lbs", "4-7 kg"));

        breed2 = new Breed("siam", "Siamese", "Vocal and social", "Thailand", "TH", "TH",
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

        breed4 = new Breed("pers", "Persian", "Calm, gentle, quiet", "Iran", "IR", "IR",
                "Long-haired cat with a sweet personality and flat face", "12-17 years", 1, 1, "Persian Longhair, Shirazi",
                3, 4, 2, 2, 1, 5, 4, 3, 4, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0,
                "https://en.wikipedia.org/wiki/Persian_cat", 0,
                "http://cfa.org/Breeds/BreedsKthruR/Persian.aspx",
                "http://www.vetstreet.com/cats/persian",
                "https://vcahospitals.com/know-your-pet/cat-breeds/persian",
                "xnzvXW6gO", new Breed.Weight("7-12 lbs", "3-5 kg"));

        category1 = new Category(1, "hats");
        category2 = new Category(2, "sunglasses");

        breedImage1 = new BreedImage("O3btzLlnp", "https://example.com/O3btzLlnp.jpg", 800, 600, false, breed1, null);
        breedImage2 = new BreedImage("ai5JRev4n", "https://example.com/ai5JRev4n.jpg", 1024, 768, false, breed2, null);
        breedImage3 = new BreedImage("O3bt3L4R1", "https://example.com/O3bt3L4R1.jpg", 1024, 768, false, null, category1);
        breedImage4 = new BreedImage("xnzvXW6gO", "https://example.com/xnzvXW6gO.jpg", 1024, 768, false, null, category2);
    }

    @Test
    void getAllBreeds_ShouldReturnListOfBreeds_WhenSuccessful() {
        // Arrange
        List<Breed> breeds = List.of(breed1, breed2, breed3, breed4);
        CompletableFuture<List<Breed>> futureBreeds = CompletableFuture.completedFuture(breeds);

        when(theCatAPIClient.getAllBreedsAsync()).thenReturn(futureBreeds);

        // Act
        CompletableFuture<ResponseEntity<List<Breed>>> result = externalApiController.getAllBreeds();

        // Assert
        ResponseEntity<List<Breed>> response = result.join();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(breeds, response.getBody());
        verify(theCatAPIClient).getAllBreedsAsync();
    }

    @Test
    void getAllBreeds_ShouldReturnInternalServerError_WhenExceptionOccurs() {
        // Arrange
        RuntimeException exception = new RuntimeException("API Error");
        CompletableFuture<List<Breed>> futureException = CompletableFuture.failedFuture(exception);

        when(theCatAPIClient.getAllBreedsAsync()).thenReturn(futureException);

        // Act
        CompletableFuture<ResponseEntity<List<Breed>>> result = externalApiController.getAllBreeds();

        // Assert
        ResponseEntity<List<Breed>> response = result.join();
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(theCatAPIClient).getAllBreedsAsync();
    }

    @Test
    void getAllBreedsImages_ShouldReturnListOfBreedImages_WhenSuccessful() {
        // Arrange
        List<Breed> breeds = List.of(breed1, breed2, breed3, breed4);
        List<BreedImage> breedImages = List.of(breedImage1, breedImage2, breedImage3, breedImage4);

        CompletableFuture<List<Breed>> futureBreeds = CompletableFuture.completedFuture(breeds);
        CompletableFuture<List<BreedImage>> futureImages = CompletableFuture.completedFuture(breedImages);

        when(theCatAPIClient.getAllBreedsAsync()).thenReturn(futureBreeds);
        when(theCatAPIClient.getBreedImagesAsync(breeds)).thenReturn(futureImages);

        // Act
        CompletableFuture<ResponseEntity<List<BreedImage>>> result = externalApiController.getAllBreedsImages();

        // Assert
        ResponseEntity<List<BreedImage>> response = result.join();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(breedImages, response.getBody());
        verify(theCatAPIClient).getAllBreedsAsync();
        verify(theCatAPIClient).getBreedImagesAsync(breeds);
    }

    @Test
    void getAllBreedsImages_ShouldReturnInternalServerError_WhenGetBreedsFails() {
        // Arrange
        RuntimeException exception = new RuntimeException("Breeds API Error");
        CompletableFuture<List<Breed>> futureException = CompletableFuture.failedFuture(exception);

        when(theCatAPIClient.getAllBreedsAsync()).thenReturn(futureException);

        // Act
        CompletableFuture<ResponseEntity<List<BreedImage>>> result = externalApiController.getAllBreedsImages();

        // Assert
        ResponseEntity<List<BreedImage>> response = result.join();
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(theCatAPIClient).getAllBreedsAsync();
        verify(theCatAPIClient, never()).getBreedImagesAsync(anyList());
    }

    @Test
    void getAllBreedsImages_ShouldReturnInternalServerError_WhenGetImagesFails() {
        // Arrange
        List<Breed> breeds = List.of(breed1, breed2, breed3, breed4);
        RuntimeException exception = new RuntimeException("Images API Error");

        CompletableFuture<List<Breed>> futureBreeds = CompletableFuture.completedFuture(breeds);
        CompletableFuture<List<BreedImage>> futureException = CompletableFuture.failedFuture(exception);

        when(theCatAPIClient.getAllBreedsAsync()).thenReturn(futureBreeds);
        when(theCatAPIClient.getBreedImagesAsync(breeds)).thenReturn(futureException);

        // Act
        CompletableFuture<ResponseEntity<List<BreedImage>>> result = externalApiController.getAllBreedsImages();

        // Assert
        ResponseEntity<List<BreedImage>> response = result.join();
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(theCatAPIClient).getAllBreedsAsync();
        verify(theCatAPIClient).getBreedImagesAsync(breeds);
    }

    @Test
    void updateCategoryCache_ShouldReturnListOfCategories_WhenSuccessful() {
        // Arrange
        List<Category> categories = List.of(category1, category2);
        CompletableFuture<List<Category>> futureCategories = CompletableFuture.completedFuture(categories);

        when(theCatAPIClient.getCategoriesAsync()).thenReturn(futureCategories);

        // Act
        CompletableFuture<ResponseEntity<List<Category>>> result = externalApiController.updateCategoryCache();

        // Assert
        ResponseEntity<List<Category>> response = result.join();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(categories, response.getBody());
        verify(theCatAPIClient).getCategoriesAsync();
    }

    @Test
    void updateCategoryCache_ShouldReturnInternalServerError_WhenExceptionOccurs() {
        // Arrange
        RuntimeException exception = new RuntimeException("Categories API Error");
        CompletableFuture<List<Category>> futureException = CompletableFuture.failedFuture(exception);

        when(theCatAPIClient.getCategoriesAsync()).thenReturn(futureException);

        // Act
        CompletableFuture<ResponseEntity<List<Category>>> result = externalApiController.updateCategoryCache();

        // Assert
        ResponseEntity<List<Category>> response = result.join();
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(theCatAPIClient).getCategoriesAsync();
    }

    @Test
    void collectData_ShouldReturnOk_WhenSuccessful() {
        // Arrange
        CompletableFuture<Void> futureVoid = CompletableFuture.completedFuture(null);

        when(dataCollectionService.collectAllData()).thenReturn(futureVoid);

        // Act
        CompletableFuture<ResponseEntity<Void>> result = externalApiController.collectData();

        // Assert
        ResponseEntity<Void> response = result.join();
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(dataCollectionService).collectAllData();
    }

    @Test
    void collectData_ShouldReturnInternalServerError_WhenExceptionOccurs() {
        // Arrange
        RuntimeException exception = new RuntimeException("Data collection error");
        CompletableFuture<Void> futureException = CompletableFuture.failedFuture(exception);

        when(dataCollectionService.collectAllData()).thenReturn(futureException);

        // Act
        CompletableFuture<ResponseEntity<Void>> result = externalApiController.collectData();

        // Assert
        ResponseEntity<Void> response = result.join();
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(dataCollectionService).collectAllData();
    }

    @Test
    void collectData_ShouldHandleCompletionException() {
        // Arrange
        CompletionException completionException = new CompletionException(new RuntimeException("Wrapped error"));
        CompletableFuture<Void> futureException = CompletableFuture.failedFuture(completionException);

        when(dataCollectionService.collectAllData()).thenReturn(futureException);

        // Act
        CompletableFuture<ResponseEntity<Void>> result = externalApiController.collectData();

        // Assert
        ResponseEntity<Void> response = result.join();
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(dataCollectionService).collectAllData();
    }
}
