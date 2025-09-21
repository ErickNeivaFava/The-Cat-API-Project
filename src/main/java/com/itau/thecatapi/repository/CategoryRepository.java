package com.itau.thecatapi.repository;

import com.itau.thecatapi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);

    List<Category> findByNameIn(List<String> names);

    @Query("SELECT c FROM Category c ORDER BY c.id")
    List<Category> findAllOrderedById();
}
