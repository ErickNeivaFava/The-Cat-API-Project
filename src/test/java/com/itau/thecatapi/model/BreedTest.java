package com.itau.thecatapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BreedTest {

    private Breed breed;
    private Breed.Weight weight;

    @BeforeEach
    void setUp() {
        weight = new Breed.Weight("7-15", "3-7");
        breed = new Breed(
                "abys",
                "Abyssinian",
                "Active, Curious, Playful",
                "Egypt",
                "EG",
                "EG",
                "The Abyssinian is easy to care for...",
                "14-15 years",
                0,
                1,
                "Abys",
                5,
                5,
                3,
                4,
                5,
                3,
                2,
                5,
                3,
                5,
                3,
                4,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                "https://en.wikipedia.org/wiki/Abyssinian_(cat)",
                0,
                "http://cfa.org/Breeds/BreedsAB/Abyssinian.aspx",
                "http://www.vetstreet.com/cats/abyssinian",
                "https://vcahospitals.com/know-your-pet/cat-breeds/abyssinian",
                "0XYvRd7oD",
                weight
        );
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        Breed defaultBreed = new Breed();
        assertNotNull(defaultBreed);
        assertNull(defaultBreed.getId());
        assertNull(defaultBreed.getName());
    }

    @Test
    @DisplayName("Test parameterized constructor")
    void testParameterizedConstructor() {
        assertEquals("abys", breed.getId());
        assertEquals("Abyssinian", breed.getName());
        assertEquals("Active, Curious, Playful", breed.getTemperament());
        assertEquals("Egypt", breed.getOrigin());
        assertEquals(weight, breed.getWeight());
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Test ID getter and setter")
        void testIdGetterSetter() {
            breed.setId("new_id");
            assertEquals("new_id", breed.getId());
        }

        @Test
        @DisplayName("Test name getter and setter")
        void testNameGetterSetter() {
            breed.setName("Siamese");
            assertEquals("Siamese", breed.getName());
        }

        @Test
        @DisplayName("Test temperament getter and setter")
        void testTemperamentGetterSetter() {
            breed.setTemperament("Calm, Gentle");
            assertEquals("Calm, Gentle", breed.getTemperament());
        }

        @Test
        @DisplayName("Test weight getter and setter")
        void testWeightGetterSetter() {
            Breed.Weight newWeight = new Breed.Weight("8-12", "4-6");
            breed.setWeight(newWeight);
            assertEquals(newWeight, breed.getWeight());
            assertEquals("8-12", breed.getWeight().getImperial());
            assertEquals("4-6", breed.getWeight().getMetric());
        }

        @Test
        @DisplayName("Test boolean-like integer fields")
        void testBooleanLikeFields() {
            breed.setIndoor(1);
            breed.setLap(0);
            breed.setExperimental(1);
            breed.setHairless(0);
            breed.setHypoallergenic(1);

            assertEquals(1, breed.getIndoor());
            assertEquals(0, breed.getLap());
            assertEquals(1, breed.getExperimental());
            assertEquals(0, breed.getHairless());
            assertEquals(1, breed.getHypoallergenic());
        }

        @Test
        @DisplayName("Test rating fields (1-5 scale)")
        void testRatingFields() {
            breed.setAdaptability(5);
            breed.setAffectionLevel(4);
            breed.setChildFriendly(3);
            breed.setEnergyLevel(2);
            breed.setIntelligence(1);

            assertEquals(5, breed.getAdaptability());
            assertEquals(4, breed.getAffectionLevel());
            assertEquals(3, breed.getChildFriendly());
            assertEquals(2, breed.getEnergyLevel());
            assertEquals(1, breed.getIntelligence());
        }

        @Test
        @DisplayName("Test URL fields")
        void testUrlFields() {
            breed.setWikipediaUrl("https://wikipedia.org/test");
            breed.setCfaUrl("https://cfa.org/test");
            breed.setVetstreetUrl("https://vetstreet.com/test");
            breed.setVcahospitalsUrl("https://vcahospitals.com/test");

            assertEquals("https://wikipedia.org/test", breed.getWikipediaUrl());
            assertEquals("https://cfa.org/test", breed.getCfaUrl());
            assertEquals("https://vetstreet.com/test", breed.getVetstreetUrl());
            assertEquals("https://vcahospitals.com/test", breed.getVcahospitalsUrl());
        }
    }

    @Nested
    @DisplayName("Weight Inner Class Tests")
    class WeightClassTests {

        @Test
        @DisplayName("Test Weight default constructor")
        void testWeightDefaultConstructor() {
            Breed.Weight weight = new Breed.Weight();
            assertNotNull(weight);
            assertNull(weight.getImperial());
            assertNull(weight.getMetric());
        }

        @Test
        @DisplayName("Test Weight parameterized constructor")
        void testWeightParameterizedConstructor() {
            Breed.Weight weight = new Breed.Weight("10-20", "5-9");
            assertEquals("10-20", weight.getImperial());
            assertEquals("5-9", weight.getMetric());
        }

        @Test
        @DisplayName("Test Weight getter and setter methods")
        void testWeightGetterSetters() {
            Breed.Weight weight = new Breed.Weight();
            weight.setImperial("8-15");
            weight.setMetric("4-7");

            assertEquals("8-15", weight.getImperial());
            assertEquals("4-7", weight.getMetric());
        }
    }

//    @Test
//    @DisplayName("Test toString method with Lombok @Data")
//    void testToStringMethod() {
//        String toStringResult = breed.toString();
//        assertNotNull(toStringResult);
//        assertTrue(toStringResult.contains("Abyssinian"));
//        assertTrue(toStringResult.contains("abys"));
//    }

//    @Test
//    @DisplayName("Test equals and hashCode methods with Lombok @Data")
//    void testEqualsAndHashCode() {
//        Breed.Weight sameWeight = new Breed.Weight("7-15", "3-7");
//        Breed sameBreed = new Breed(
//                "abys",
//                "Abyssinian",
//                "Active, Curious, Playful",
//                "Egypt",
//                "EG",
//                "EG",
//                "The Abyssinian is easy to care for...",
//                "14-15 years",
//                0,
//                1,
//                "Abys",
//                5,
//                5,
//                3,
//                4,
//                5,
//                3,
//                2,
//                5,
//                3,
//                5,
//                3,
//                4,
//                0,
//                0,
//                0,
//                0,
//                0,
//                0,
//                0,
//                "https://en.wikipedia.org/wiki/Abyssinian_(cat)",
//                0,
//                "http://cfa.org/Breeds/BreedsAB/Abyssinian.aspx",
//                "http://www.vetstreet.com/cats/abyssinian",
//                "https://vcahospitals.com/know-your-pet/cat-breeds/abyssinian",
//                "0XYvRd7oD",
//                sameWeight
//        );
//
//        Breed differentBreed = new Breed();
//        differentBreed.setId("beng");
//        differentBreed.setName("Bengal");
//
//        assertEquals(breed, sameBreed);
//        assertNotEquals(breed, differentBreed);
//        assertEquals(breed.hashCode(), sameBreed.hashCode());
//        assertNotEquals(breed.hashCode(), differentBreed.hashCode());
//    }

    @Test
    @DisplayName("Test edge cases for integer fields")
    void testEdgeCasesForIntegerFields() {
        // Test null values
        breed.setIndoor(null);
        breed.setAdaptability(null);

        assertNull(breed.getIndoor());
        assertNull(breed.getAdaptability());

        // Test boundary values for ratings (should be 1-5)
        breed.setAffectionLevel(1);
        breed.setChildFriendly(5);

        assertEquals(1, breed.getAffectionLevel());
        assertEquals(5, breed.getChildFriendly());
    }

    @Test
    @DisplayName("Test string field length constraints")
    void testStringFieldLengthConstraints() {
        // Test description field with long text
        String longDescription = "A".repeat(1000);
        breed.setDescription(longDescription);
        assertEquals(1000, breed.getDescription().length());

        // Test with null values
        breed.setDescription(null);
        breed.setTemperament(null);

        assertNull(breed.getDescription());
        assertNull(breed.getTemperament());
    }

    @Test
    @DisplayName("Test country code fields")
    void testCountryCodeFields() {
        breed.setCountryCodes("US,CA");
        breed.setCountryCode("US");

        assertEquals("US,CA", breed.getCountryCodes());
        assertEquals("US", breed.getCountryCode());
    }

    @Test
    @DisplayName("Test life span field")
    void testLifeSpanField() {
        breed.setLifeSpan("12-16 years");
        assertEquals("12-16 years", breed.getLifeSpan());
    }

    @Test
    @DisplayName("Test alternative names field")
    void testAltNamesField() {
        breed.setAltNames("Abys, Aby");
        assertEquals("Abys, Aby", breed.getAltNames());
    }

    @Test
    @DisplayName("Test reference image ID field")
    void testReferenceImageIdField() {
        breed.setReferenceImageId("new_image_id");
        assertEquals("new_image_id", breed.getReferenceImageId());
    }
}
