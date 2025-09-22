package com.itau.thecatapi.repository;

import com.itau.thecatapi.model.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedRepository extends JpaRepository<Breed, String> {

    List<Breed> findByTemperamentContainingIgnoreCase(String temperament);

    List<Breed> findByOriginContainingIgnoreCase(String origin);

    List<Breed> findByTemperamentContainingAndOriginContainingIgnoreCase(String temperament, String origin);
}
