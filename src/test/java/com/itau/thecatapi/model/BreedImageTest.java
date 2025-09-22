package com.itau.thecatapi.model;

import com.itau.thecatapi.dto.BreedImageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.annotation.JsonProperty;

class BreedImageTest {

    private BreedImage breedImage;
    private Breed breed;
    private Category category;

    @BeforeEach
    void setUp() {
        breed = new Breed();
        breed.setId("breed123");

        category = new Category();
        category.setId(1);

        breedImage = new BreedImage(
                "img123",
                "https://example.com/image.jpg",
                800,
                600,
                false,
                breed,
                category
        );
    }

    @Test
    void testDefaultConstructor() {
        BreedImage emptyImage = new BreedImage();
        assertNotNull(emptyImage);
        assertNull(emptyImage.getId());
        assertNull(emptyImage.getUrl());
        assertNull(emptyImage.getWidth());
        assertNull(emptyImage.getHeight());
        assertNull(emptyImage.getBreed());
        assertNull(emptyImage.getCategoryIdOnly());
        assertEquals(false, emptyImage.getFavourite()); // Valor padrão
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("img123", breedImage.getId());
        assertEquals("https://example.com/image.jpg", breedImage.getUrl());
        assertEquals(800, breedImage.getWidth());
        assertEquals(600, breedImage.getHeight());
        assertEquals(false, breedImage.getFavourite());
        assertEquals(breed, breedImage.getBreed());
        assertEquals(category, breedImage.getCategory());
    }

    @Test
    void testSettersAndGetters() {
        BreedImage image = new BreedImage();

        image.setId("newId");
        image.setUrl("https://new-url.com");
        image.setWidth(1024);
        image.setHeight(768);
        image.setFavourite(true);
        image.setBreed(breed);
        image.setCategory(category);

        assertEquals("newId", image.getId());
        assertEquals("https://new-url.com", image.getUrl());
        assertEquals(1024, image.getWidth());
        assertEquals(768, image.getHeight());
        assertEquals(true, image.getFavourite());
        assertEquals(breed, image.getBreed());
        assertEquals(category, image.getCategory());
    }

    @Test
    void testGetBreedIdOnly_WithBreed() {
        assertEquals("breed123", breedImage.getBreedIdOnly());
    }

    @Test
    void testGetBreedIdOnly_WithoutBreed() {
        BreedImage imageWithoutBreed = new BreedImage();
        imageWithoutBreed.setBreed(null);
        assertNull(imageWithoutBreed.getBreedIdOnly());
    }

    @Test
    void testGetCategoryIdOnly_WithCategory() {
        assertEquals(1, breedImage.getCategoryIdOnly());
    }

    @Test
    void testGetCategoryIdOnly_WithoutCategory() {
        BreedImage imageWithoutCategory = new BreedImage();
        imageWithoutCategory.setCategory(null);
        assertNull(imageWithoutCategory.getCategoryIdOnly());
    }

    @Test
    void testFromResponse_WithAllData() {
        BreedImageDTO response = new BreedImageDTO();
        response.setId("responseId");
        response.setUrl("https://response.com");
        response.setWidth(500);
        response.setHeight(400);
        response.setFavourite(true);

        Breed responseBreed = new Breed();
        responseBreed.setId("responseBreed");
        response.setBreeds(java.util.Arrays.asList(responseBreed));

        Category responseCategory = new Category();
        responseCategory.setId(2);
        response.setCategories(java.util.Arrays.asList(responseCategory));

        BreedImage result = BreedImage.fromResponse(response);

        assertEquals("responseId", result.getId());
        assertEquals("https://response.com", result.getUrl());
        assertEquals(500, result.getWidth());
        assertEquals(400, result.getHeight());
        assertEquals(true, result.getFavourite());
        assertEquals(responseBreed, result.getBreed());
        assertEquals(responseCategory, result.getCategory());
    }

    @Test
    void testFromResponse_WithoutBreeds() {
        BreedImageDTO response = new BreedImageDTO();
        response.setId("responseId");
        response.setUrl("https://response.com");
        response.setWidth(500);
        response.setHeight(400);
        response.setFavourite(false);
        response.setBreeds(null);

        Category responseCategory = new Category();
        responseCategory.setId(2);
        response.setCategories(java.util.Arrays.asList(responseCategory));

        BreedImage result = BreedImage.fromResponse(response);

        assertEquals("responseId", result.getId());
        assertEquals("https://response.com", result.getUrl());
        assertEquals(500, result.getWidth());
        assertEquals(400, result.getHeight());
        assertEquals(false, result.getFavourite());
        assertNull(result.getBreed());
        assertEquals(responseCategory, result.getCategory());
    }

    @Test
    void testFromResponse_WithoutCategories() {
        BreedImageDTO response = new BreedImageDTO();
        response.setId("responseId");
        response.setUrl("https://response.com");
        response.setWidth(500);
        response.setHeight(400);
        response.setFavourite(false);

        Breed responseBreed = new Breed();
        responseBreed.setId("responseBreed");
        response.setBreeds(java.util.Arrays.asList(responseBreed));
        response.setCategories(null);

        BreedImage result = BreedImage.fromResponse(response);

        assertEquals("responseId", result.getId());
        assertEquals("https://response.com", result.getUrl());
        assertEquals(500, result.getWidth());
        assertEquals(400, result.getHeight());
        assertEquals(false, result.getFavourite());
        assertEquals(responseBreed, result.getBreed());
        assertNull(result.getCategory());
    }

    @Test
    void testFromResponse_WithEmptyBreedsList() {
        BreedImageDTO response = new BreedImageDTO();
        response.setId("responseId");
        response.setUrl("https://response.com");
        response.setWidth(500);
        response.setHeight(400);
        response.setFavourite(false);
        response.setBreeds(java.util.Collections.emptyList());

        Category responseCategory = new Category();
        responseCategory.setId(2);
        response.setCategories(java.util.Arrays.asList(responseCategory));

        BreedImage result = BreedImage.fromResponse(response);

        assertNull(result.getBreed());
        assertEquals(responseCategory, result.getCategory());
    }

    @Test
    void testFromResponse_WithEmptyCategoriesList() {
        BreedImageDTO response = new BreedImageDTO();
        response.setId("responseId");
        response.setUrl("https://response.com");
        response.setWidth(500);
        response.setHeight(400);
        response.setFavourite(false);

        Breed responseBreed = new Breed();
        responseBreed.setId("responseBreed");
        response.setBreeds(java.util.Arrays.asList(responseBreed));
        response.setCategories(java.util.Collections.emptyList());

        BreedImage result = BreedImage.fromResponse(response);

        assertEquals(responseBreed, result.getBreed());
        assertNull(result.getCategory());
    }

    @Test
    void testFavouriteDefaultValue() {
        BreedImage image = new BreedImage();
        assertEquals(false, image.getFavourite()); // Deve ser false por padrão
    }
}
