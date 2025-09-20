package com.itau.thecatapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HttpUtils {

    private final String apiKey;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public HttpUtils(
            @Value("${thecatapi.api-key}") String apiKey,
            @Value("${thecatapi.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.objectMapper = createObjectMapper();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String buildUrl(String endpoint) {
        return baseUrl + endpoint;
    }

    public HttpEntity<String> createDefaultEntity() {
        return new HttpEntity<>(createDefaultHeaders());
    }

    public HttpEntity<String> createEntityWithBody(Object body) {
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            return new HttpEntity<>(jsonBody, createDefaultHeaders());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar objeto", e);
        }
    }

    public HttpHeaders createDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-api-key", apiKey);
        return headers;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
