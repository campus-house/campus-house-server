package com.example.campus_house.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NaverApiConfig {
    
    @Value("${naver.api.client-id}")
    private String clientId;
    
    @Value("${naver.api.client-secret}")
    private String clientSecret;
    
    @Value("${naver.api.map-url}")
    private String mapUrl;
    
    @Value("${naver.api.search-url}")
    private String searchUrl;
    
    @Value("${naver.api.style-code}")
    private String styleCode;
    
    @Value("${naver.api.version}")
    private String version;
    
    @Bean
    public WebClient naverMapWebClient() {
        return WebClient.builder()
                .baseUrl(mapUrl)
                .defaultHeader("X-NCP-APIGW-API-KEY-ID", clientId)
                .defaultHeader("X-NCP-APIGW-API-KEY", clientSecret)
                .build();
    }
    
    @Bean
    public WebClient naverSearchWebClient() {
        return WebClient.builder()
                .baseUrl(searchUrl)
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public String getMapUrl() {
        return mapUrl;
    }
    
    public String getSearchUrl() {
        return searchUrl;
    }
    
    public String getStyleCode() {
        return styleCode;
    }
    
    public String getVersion() {
        return version;
    }
}
