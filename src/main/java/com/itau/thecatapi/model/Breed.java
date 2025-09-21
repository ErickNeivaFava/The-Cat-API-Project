package com.itau.thecatapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "breeds")
@Data
@DynamicUpdate
public class Breed {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "temperament")
    private String temperament;

    @Column(name = "origin")
    private String origin;

    @Column(name = "country_codes")
    private String countryCodes;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "life_span")
    private String lifeSpan;

    @Column(name = "indoor")
    private Integer indoor;

    @Column(name = "lap")
    private Integer lap;

    @Column(name = "alt_names")
    private String altNames;

    @Column(name = "adaptability")
    private Integer adaptability;

    @Column(name = "affection_level")
    private Integer affectionLevel;

    @Column(name = "child_friendly")
    private Integer childFriendly;

    @Column(name = "dog_friendly")
    private Integer dogFriendly;

    @Column(name = "energy_level")
    private Integer energyLevel;

    @Column(name = "grooming")
    private Integer grooming;

    @Column(name = "health_issues")
    private Integer healthIssues;

    @Column(name = "intelligence")
    private Integer intelligence;

    @Column(name = "shedding_level")
    private Integer sheddingLevel;

    @Column(name = "social_needs")
    private Integer socialNeeds;

    @Column(name = "stranger_friendly")
    private Integer strangerFriendly;

    @Column(name = "vocalisation")
    private Integer vocalisation;

    @Column(name = "experimental")
    private Integer experimental;

    @Column(name = "hairless")
    private Integer hairless;

    @Column(name = "\"natural\"") // natural é uma palavra chave reservada do PostgreSQL -> TODO: verificar se é boolean na documentação e alterar para isNatural
    private Integer natural;

    @Column(name = "rare")
    private Integer rare;

    @Column(name = "rex")
    private Integer rex;

    @Column(name = "suppressed_tail")
    private Integer suppressedTail;

    @Column(name = "short_legs")
    private Integer shortLegs;

    @Column(name = "wikipedia_url")
    private String wikipediaUrl;

    @Column(name = "hypoallergenic")
    private Integer hypoallergenic;

    @Column(name = "cfa_url")
    private String cfaUrl;

    @Column(name = "vetstreet_url")
    private String vetstreetUrl;

    @Column(name = "vcahospitals_url")
    private String vcahospitalsUrl;

    @Column(name = "reference_image_id")
    private String referenceImageId;

    @Embedded
    private Weight weight;


    //@Embedded
    //private CatImage image;

    public Breed() {
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

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(String countryCodes) {
        this.countryCodes = countryCodes;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(String lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public Integer getIndoor() {
        return indoor;
    }

    public void setIndoor(Integer indoor) {
        this.indoor = indoor;
    }

    public Integer getLap() {
        return lap;
    }

    public void setLap(Integer lap) {
        this.lap = lap;
    }

    public String getAltNames() {
        return altNames;
    }

    public void setAltNames(String altNames) {
        this.altNames = altNames;
    }

    public Integer getAdaptability() {
        return adaptability;
    }

    public void setAdaptability(Integer adaptability) {
        this.adaptability = adaptability;
    }

    public Integer getAffectionLevel() {
        return affectionLevel;
    }

    public void setAffectionLevel(Integer affectionLevel) {
        this.affectionLevel = affectionLevel;
    }

    public Integer getChildFriendly() {
        return childFriendly;
    }

    public void setChildFriendly(Integer childFriendly) {
        this.childFriendly = childFriendly;
    }

    public Integer getDogFriendly() {
        return dogFriendly;
    }

    public void setDogFriendly(Integer dogFriendly) {
        this.dogFriendly = dogFriendly;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(Integer energyLevel) {
        this.energyLevel = energyLevel;
    }

    public Integer getGrooming() {
        return grooming;
    }

    public void setGrooming(Integer grooming) {
        this.grooming = grooming;
    }

    public Integer getHealthIssues() {
        return healthIssues;
    }

    public void setHealthIssues(Integer healthIssues) {
        this.healthIssues = healthIssues;
    }

    public Integer getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(Integer intelligence) {
        this.intelligence = intelligence;
    }

    public Integer getSheddingLevel() {
        return sheddingLevel;
    }

    public void setSheddingLevel(Integer sheddingLevel) {
        this.sheddingLevel = sheddingLevel;
    }

    public Integer getSocialNeeds() {
        return socialNeeds;
    }

    public void setSocialNeeds(Integer socialNeeds) {
        this.socialNeeds = socialNeeds;
    }

    public Integer getStrangerFriendly() {
        return strangerFriendly;
    }

    public void setStrangerFriendly(Integer strangerFriendly) {
        this.strangerFriendly = strangerFriendly;
    }

    public Integer getVocalisation() {
        return vocalisation;
    }

    public void setVocalisation(Integer vocalisation) {
        this.vocalisation = vocalisation;
    }

    public Integer getExperimental() {
        return experimental;
    }

    public void setExperimental(Integer experimental) {
        this.experimental = experimental;
    }

    public Integer getHairless() {
        return hairless;
    }

    public void setHairless(Integer hairless) {
        this.hairless = hairless;
    }

    public Integer getNatural() {
        return natural;
    }

    public void setNatural(Integer natural) {
        this.natural = natural;
    }

    public Integer getRare() {
        return rare;
    }

    public void setRare(Integer rare) {
        this.rare = rare;
    }

    public Integer getRex() {
        return rex;
    }

    public void setRex(Integer rex) {
        this.rex = rex;
    }

    public Integer getSuppressedTail() {
        return suppressedTail;
    }

    public void setSuppressedTail(Integer suppressedTail) {
        this.suppressedTail = suppressedTail;
    }

    public Integer getShortLegs() {
        return shortLegs;
    }

    public void setShortLegs(Integer shortLegs) {
        this.shortLegs = shortLegs;
    }

    public String getWikipediaUrl() {
        return wikipediaUrl;
    }

    public void setWikipediaUrl(String wikipediaUrl) {
        this.wikipediaUrl = wikipediaUrl;
    }

    public Integer getHypoallergenic() {
        return hypoallergenic;
    }

    public void setHypoallergenic(Integer hypoallergenic) {
        this.hypoallergenic = hypoallergenic;
    }

    public String getCfaUrl() {
        return cfaUrl;
    }

    public void setCfaUrl(String cfaUrl) {
        this.cfaUrl = cfaUrl;
    }

    public String getVetstreetUrl() {
        return vetstreetUrl;
    }

    public void setVetstreetUrl(String vetstreetUrl) {
        this.vetstreetUrl = vetstreetUrl;
    }

    public String getVcahospitalsUrl() {
        return vcahospitalsUrl;
    }

    public void setVcahospitalsUrl(String vcahospitalsUrl) {
        this.vcahospitalsUrl = vcahospitalsUrl;
    }

    public String getReferenceImageId() {
        return referenceImageId;
    }

    public void setReferenceImageId(String referenceImageId) {
        this.referenceImageId = referenceImageId;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

//    public CatImage getImage() {
//        return image;
//    }
//
//    public void setImage(CatImage image) {
//        this.image = image;
//    }

    @Embeddable
    public static class Weight {

        @Column(name = "weight_imperial")
        private String imperial;

        @Column(name = "weight_metric")
        private String metric;

        public Weight() {
        }

        public Weight(String imperial, String metric) {
            this.imperial = imperial;
            this.metric = metric;
        }

        public String getImperial() {
            return imperial;
        }

        public void setImperial(String imperial) {
            this.imperial = imperial;
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }
    }

//    @Embeddable
//    public static class CatImage {
//
//        @Column(name = "image_id")
//        private String id;
//
//        @Column(name = "image_width")
//        private Integer width;
//
//        @Column(name = "image_height")
//        private Integer height;
//
//        @Column(name = "image_url")
//        private String url;
//
//        public CatImage() {
//        }
//
//        public CatImage(String id, Integer width, Integer height, String url) {
//            this.id = id;
//            this.width = width;
//            this.height = height;
//            this.url = url;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public Integer getWidth() {
//            return width;
//        }
//
//        public void setWidth(Integer width) {
//            this.width = width;
//        }
//
//        public Integer getHeight() {
//            return height;
//        }
//
//        public void setHeight(Integer height) {
//            this.height = height;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
//    }

}