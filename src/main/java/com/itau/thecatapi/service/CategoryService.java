package com.itau.thecatapi.service;

import com.itau.thecatapi.model.Category;
import com.itau.thecatapi.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void initCache() {
        getAllCategoriesMap();
    }

    @Cacheable(value = "categories", key = "'allCategories'")
    public List<Category> getAllCategories() {
        return categoryRepository.findAllOrderedById();
    }

    @Cacheable(value = "categories", key = "'categoriesMap'")
    public Map<Integer, String> getAllCategoriesMap() {
        return categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        Category::getName
                ));
    }

    @Cacheable(value = "categories", key = "'category-' + #id")
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Cacheable(value = "categories", key = "'categoryByName-' + #name")
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Cacheable(value = "categories", key = "'categoryNameById-' + #id")
    public String getCategoryNameById(Integer id) {
        return categoryRepository.findById(id)
                .map(Category::getName)
                .orElse(null);
    }

    @Cacheable(value = "categories", key = "'categoriesByNames-' + #names.hashCode()")
    public List<Category> getCategoriesByNames(List<String> names) {
        return categoryRepository.findByNameIn(names);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void clearCache() {
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
