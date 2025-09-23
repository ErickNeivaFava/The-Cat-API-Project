package com.itau.thecatapi.message;

import java.time.LocalDateTime;

public class BreedRequestMessage {
    private String messageId;
    private String email;
    private String breedId;
    private String origin;
    private String temperament;
    private LocalDateTime requestTime;

    public BreedRequestMessage() {
        this.requestTime = LocalDateTime.now();
    }

    public BreedRequestMessage(String email, String breedId, String origin, String temperament) {
        this();
        this.email = email;
        this.breedId = breedId;
        this.origin = origin;
        this.temperament = temperament;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBreedId() { return breedId; }
    public void setBreedId(String breedId) { this.breedId = breedId; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getTemperament() { return temperament; }
    public void setTemperament(String temperament) { this.temperament = temperament; }

    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
}
