package com.itau.thecatapi.service;

import com.itau.thecatapi.model.Category;
import com.itau.thecatapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategoriesMapWithFullData() {
        List<Category> categories = List.of(
                new Category(5, "boxes"),
                new Category(15, "clothes"),
                new Category(1, "hats"),
                new Category(14, "sinks"),
                new Category(2, "space"),
                new Category(4, "sunglasses"),
                new Category(7, "ties")
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        Map<Integer, String> result = categoryService.getAllCategoriesMap();

        assertEquals(7, result.size());
        assertEquals("boxes", result.get(5));
        assertEquals("clothes", result.get(15));
        assertEquals("hats", result.get(1));
        assertEquals("sinks", result.get(14));
        assertEquals("space", result.get(2));
        assertEquals("sunglasses", result.get(4));
        assertEquals("ties", result.get(7));
    }


    @Test
    void testGetAllCategories() {
        List<Category> categories = List.of(new Category(1, "Funny"), new Category(2, "Cute"));
        when(categoryRepository.findAllOrderedById()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Funny", result.get(0).getName());
        verify(categoryRepository).findAllOrderedById();
    }

    @Test
    void testGetAllCategoriesMap() {
        List<Category> categories = List.of(new Category(1, "Funny"), new Category(2, "Cute"));
        when(categoryRepository.findAll()).thenReturn(categories);

        Map<Integer, String> result = categoryService.getAllCategoriesMap();

        assertEquals("Funny", result.get(1));
        assertEquals("Cute", result.get(2));
        verify(categoryRepository).findAll();
    }

    @Test
    void testGetCategoryById() {
        Category category = new Category(1, "Funny");
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(1);

        assertTrue(result.isPresent());
        assertEquals("Funny", result.get().getName());
    }

    @Test
    void testGetCategoryByName() {
        Category category = new Category(1, "Funny");
        when(categoryRepository.findByName("Funny")).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryByName("Funny");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testGetCategoryNameById() {
        Category category = new Category(1, "Funny");
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        String name = categoryService.getCategoryNameById(1);

        assertEquals("Funny", name);
    }

    @Test
    void testGetCategoriesByNames() {
        List<Category> categories = List.of(new Category(1, "Funny"), new Category(2, "Cute"));
        when(categoryRepository.findByNameIn(List.of("Funny", "Cute"))).thenReturn(categories);

        List<Category> result = categoryService.getCategoriesByNames(List.of("Funny", "Cute"));

        assertEquals(2, result.size());
    }

    @Test
    void testSaveCategory() {
        Category category = new Category(1, "Funny");
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.saveCategory(category);

        assertEquals("Funny", result.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void testClearCache() {
        // Método vazio, mas testamos se não lança exceção
        assertDoesNotThrow(() -> categoryService.clearCache());
    }
}

