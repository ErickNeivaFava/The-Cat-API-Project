package com.itau.thecatapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TheCatAPIClient {

    @Value("${thecatapi.base-url}")
    private String baseUrl;

    @Value("${thecatapi.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public TheCatAPIClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

}
