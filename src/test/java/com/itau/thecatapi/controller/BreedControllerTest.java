package com.itau.thecatapi.controller;

import com.itau.thecatapi.dto.BreedResponseDTO;
import com.itau.thecatapi.exception.ResourceNotFoundException;
import com.itau.thecatapi.service.BreedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreedControllerTest {

    @Mock
    private BreedService breedService;

    @InjectMocks
    private BreedController breedController;

    private BreedResponseDTO breed1;
    private BreedResponseDTO breed2;

    @BeforeEach
    void setUp() {
        breed1 = new BreedResponseDTO("abys", "Abyssinian", "Egypt", "Active, Energetic, Independent, Intelligent, Gentle", "The Abyssinian is easy to care for, and a joy to have in your home. They’re affectionate cats and love both people and other animals.", "https://cdn2.thecatapi.com/images/itfFA4NWS.jpg");
        breed2 = new BreedResponseDTO("aege", "Aegean", "Greece", "Affectionate, Social, Intelligent, Playful, Active", "Native to the Greek islands known as the Cyclades in the Aegean Sea, these are natural cats, meaning they developed without humans getting involved in their breeding. As a breed, Aegean Cats are rare, although they are numerous on their home islands. They are generally friendly toward people and can be excellent cats for families with children.", "https://cdn2.thecatapi.com/images/h19-vtIeX.jpg");
    }

    @Test
    void getBreeds_WithTemperamentAndOrigin_ShouldReturnBreeds() throws Exception {
        // Arrange
        String temperament = "Energetic";
        String origin = "Egypt";
        List<BreedResponseDTO> breeds = List.of(breed1);

        when(breedService.getBreedsByTemperamentAndOrigin(temperament, origin))
                .thenReturn(CompletableFuture.completedFuture(breeds));

        // Act
        CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> result =
                breedController.getBreeds(temperament, origin);

        // Assert
        ResponseEntity<List<BreedResponseDTO>> response = result.get();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Abyssinian", response.getBody().get(0).getName());

        verify(breedService).getBreedsByTemperamentAndOrigin(temperament, origin);
    }

    @Test
    void getBreeds_WithTemperamentAndOrigin_NoBreedsFound_ShouldThrowException() {
        // Arrange
        String temperament = "Nonexistent";
        String origin = "Nowhere";

        when(breedService.getBreedsByTemperamentAndOrigin(temperament, origin))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        // Act & Assert
        CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> result =
                breedController.getBreeds(temperament, origin);

        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        assertEquals("Nenhuma raça encontrada para o temperamento: Nonexistent e origem: Nowhere",
                exception.getCause().getMessage());
    }

    @Test
    void getBreeds_WithTemperamentOnly_ShouldReturnBreeds() throws Exception {
        // Arrange
        String temperament = "Friendly";
        List<BreedResponseDTO> breeds = List.of(breed1, breed2);

        when(breedService.getBreedsByTemperament(temperament))
                .thenReturn(CompletableFuture.completedFuture(breeds));

        // Act
        CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> result =
                breedController.getBreeds(temperament, null);

        // Assert
        ResponseEntity<List<BreedResponseDTO>> response = result.get();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(breedService).getBreedsByTemperament(temperament);
    }

    @Test
    void getBreeds_WithOriginOnly_ShouldReturnBreeds() throws Exception {
        // Arrange
        String origin = "Egypt";
        List<BreedResponseDTO> breeds = List.of(breed1);

        when(breedService.getBreedsByOrigin(origin))
                .thenReturn(CompletableFuture.completedFuture(breeds));

        // Act
        CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> result =
                breedController.getBreeds(null, origin);

        // Assert
        ResponseEntity<List<BreedResponseDTO>> response = result.get();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Egypt", response.getBody().get(0).getOrigin());

        verify(breedService).getBreedsByOrigin(origin);
    }

    @Test
    void getBreeds_NoFilters_ShouldReturnAllBreeds() throws Exception {
        // Arrange
        List<BreedResponseDTO> breeds = List.of(breed1, breed2);

        when(breedService.getAllBreeds())
                .thenReturn(CompletableFuture.completedFuture(breeds));

        // Act
        CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> result =
                breedController.getBreeds(null, null);

        // Assert
        ResponseEntity<List<BreedResponseDTO>> response = result.get();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(breedService).getAllBreeds();
    }

    @Test
    void getBreeds_NoFilters_NoBreedsFound_ShouldThrowException() {
        // Arrange
        when(breedService.getAllBreeds())
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        // Act & Assert
        CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> result =
                breedController.getBreeds(null, null);

        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        assertEquals("Nenhuma raça encontrada no sistema", exception.getCause().getMessage());
    }

    @Test
    void getBreedById_ValidId_ShouldReturnBreed() throws Exception {
        // Arrange
        String id = "1";

        when(breedService.getBreedById(id))
                .thenReturn(CompletableFuture.completedFuture(breed1));

        // Act
        CompletableFuture<ResponseEntity<BreedResponseDTO>> result =
                breedController.getBreedById(id);

        // Assert
        ResponseEntity<BreedResponseDTO> response = result.get();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("abys", response.getBody().getId());
        assertEquals("Abyssinian", response.getBody().getName());

        verify(breedService).getBreedById(id);
    }

    @Test
    void getBreedById_NotFound_ShouldThrowException() {
        // Arrange
        String id = "999";

        when(breedService.getBreedById(id))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act & Assert
        CompletableFuture<ResponseEntity<BreedResponseDTO>> result =
                breedController.getBreedById(id);

        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        assertEquals("Raça não encontrada no sistema para o ID: 999", exception.getCause().getMessage());
    }

    @Test
    void getBreedById_BreedWithNullId_ShouldThrowException() {
        // Arrange
        String id = "invalid";
        BreedResponseDTO nullBreed = new BreedResponseDTO(null, null, null, null, null, null);

        when(breedService.getBreedById(id))
                .thenReturn(CompletableFuture.completedFuture(nullBreed));

        // Act & Assert
        CompletableFuture<ResponseEntity<BreedResponseDTO>> result =
                breedController.getBreedById(id);

        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        assertEquals("Raça não encontrada no sistema para o ID: invalid", exception.getCause().getMessage());
    }

    @Test
    void getBreeds_ServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        String temperament = "Friendly";

        when(breedService.getBreedsByTemperament(temperament))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

        // Act
        CompletableFuture<ResponseEntity<List<BreedResponseDTO>>> result =
                breedController.getBreeds(temperament, null);

        // Assert
        ResponseEntity<List<BreedResponseDTO>> response = result.get();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getBreedById_ServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        String id = "1";

        when(breedService.getBreedById(id))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

        // Act
        CompletableFuture<ResponseEntity<BreedResponseDTO>> result =
                breedController.getBreedById(id);

        // Assert
        ResponseEntity<BreedResponseDTO> response = result.get();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
