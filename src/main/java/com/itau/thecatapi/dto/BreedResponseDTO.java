package com.itau.thecatapi.dto;

public class BreedResponseDTO {
    private String id;
    private String name;
    private String origin;
    private String temperament;
    private String description;
    private String imageUrl;

    public BreedResponseDTO() {
    }

    public BreedResponseDTO(String id, String name, String origin, String temperament, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.origin = origin;
        this.temperament = temperament;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
