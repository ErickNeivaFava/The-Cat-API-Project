package com.itau.thecatapi.repository;

import com.itau.thecatapi.model.BreedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedImageRepository extends JpaRepository<BreedImage, String> {

    List<BreedImage> findByBreedId(String breedId);

    List<BreedImage> findByCategoryId(Integer categoryId);

    List<BreedImage> findByBreedIdAndCategoryId(String breedId, Integer categoryId);

    List<BreedImage> findByFavouriteTrue();
}
