package com.itau.thecatapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itau.thecatapi.dto.BreedImageDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Map;

@Entity
@Table(name = "images")
@Data
public class BreedImage {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "favourite")
    private Boolean favourite = false;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", referencedColumnName = "id")
    private Breed breed;

    @JsonProperty("breed")
    public String getBreedIdOnly() {
        return breed != null ? breed.getId() : null;
    }

    public BreedImage(String id, String url, Integer width, Integer height, Boolean favourite, Breed breed) {
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
        this.favourite = favourite;
        this.breed = breed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Breed getBreed() {
        return breed;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public static BreedImage fromResponse(BreedImageDTO response) {
        Breed breed = null;
        if (response.getBreeds() != null && !response.getBreeds().isEmpty()) {
            breed = response.getBreeds().get(0);
        }

        return new BreedImage(
                response.getId(),
                response.getUrl(),
                response.getWidth(),
                response.getHeight(),
                response.getFavourite(),
                breed
        );
    }
}
