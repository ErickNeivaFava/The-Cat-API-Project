package com.itau.thecatapi.repository;

import com.itau.thecatapi.model.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedRepository extends JpaRepository<Breed, String> {
}
