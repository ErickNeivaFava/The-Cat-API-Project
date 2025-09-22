package com.itau.thecatapi.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        Category category = new Category();

        // Assert
        assertNotNull(category);
        assertNull(category.getId());
        assertNull(category.getName());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Integer expectedId = 1;
        String expectedName = "Electronics";

        // Act
        Category category = new Category(expectedId, expectedName);

        // Assert
        assertNotNull(category);
        assertEquals(expectedId, category.getId());
        assertEquals(expectedName, category.getName());
    }

    @Test
    void testParameterizedConstructorWithNullValues() {
        // Arrange & Act
        Category category = new Category(null, null);

        // Assert
        assertNotNull(category);
        assertNull(category.getId());
        assertNull(category.getName());
    }

    @Test
    void testSetAndGetId() {
        // Arrange
        Category category = new Category();
        Integer expectedId = 42;

        // Act
        category.setId(expectedId);
        Integer actualId = category.getId();

        // Assert
        assertEquals(expectedId, actualId);
    }

    @Test
    void testSetAndGetName() {
        // Arrange
        Category category = new Category();
        String expectedName = "Books";

        // Act
        category.setName(expectedName);
        String actualName = category.getName();

        // Assert
        assertEquals(expectedName, actualName);
    }

    @Test
    void testSetIdToNull() {
        // Arrange
        Category category = new Category(1, "Test");

        // Act
        category.setId(null);

        // Assert
        assertNull(category.getId());
    }

    @Test
    void testSetNameToNull() {
        // Arrange
        Category category = new Category(1, "Test");

        // Act
        category.setName(null);

        // Assert
        assertNull(category.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Category category1 = new Category(1, "Electronics");
        Category category2 = new Category(1, "Electronics");
        Category category3 = new Category(2, "Books");

        // Assert
        assertEquals(category1, category2);
        assertNotEquals(category1, category3);
        assertEquals(category1.hashCode(), category2.hashCode());
        assertNotEquals(category1.hashCode(), category3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Category category = new Category(1, "Electronics");

        // Act
        String toStringResult = category.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("name=Electronics"));
    }

    @Test
    void testEqualsWithNull() {
        // Arrange
        Category category = new Category(1, "Test");

        // Assert
        assertNotEquals(null, category);
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Arrange
        Category category = new Category(1, "Test");
        Object differentObject = "Not a Category";

        // Assert
        assertNotEquals(category, differentObject);
    }

    @Test
    void testEqualsWithSameInstance() {
        // Arrange
        Category category = new Category(1, "Test");

        // Assert
        assertEquals(category, category);
    }
}
