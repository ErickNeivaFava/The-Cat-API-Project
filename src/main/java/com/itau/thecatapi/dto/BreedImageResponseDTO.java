package com.itau.thecatapi.dto;

public class BreedImageResponseDTO {
    private String id;
    private String url;
    private Integer width;
    private Integer height;
    private Boolean favourite;
    private String breedId;
    private String breedName;
    private Integer categoryId;
    private String categoryName;

    public BreedImageResponseDTO() {
    }

    public BreedImageResponseDTO(String id, String url, Integer width, Integer height, Boolean favourite,
                                 String breedId, String breedName, Integer categoryId, String categoryName) {
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
        this.favourite = favourite;
        this.breedId = breedId;
        this.breedName = breedName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public String getBreedId() {
        return breedId;
    }

    public void setBreedId(String breedId) {
        this.breedId = breedId;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}