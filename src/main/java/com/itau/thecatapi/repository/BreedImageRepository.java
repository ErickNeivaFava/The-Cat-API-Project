package com.itau.thecatapi.repository;

import com.itau.thecatapi.model.BreedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedImageRepository extends JpaRepository<BreedImage, String> {
}
