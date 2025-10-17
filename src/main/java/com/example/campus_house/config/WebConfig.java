package com.example.campus_house.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // API 엔드포인트용 CORS 설정 (credentials 허용)
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "https://localhost:*", "http://127.0.0.1:*", "https://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        
        // Swagger UI를 위한 CORS 설정 (credentials 허용)
        registry.addMapping("/swagger-ui/**")
                .allowedOriginPatterns("http://localhost:*", "https://localhost:*", "http://127.0.0.1:*", "https://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        
        // OpenAPI docs를 위한 CORS 설정 (credentials 허용)
        registry.addMapping("/v3/api-docs/**")
                .allowedOriginPatterns("http://localhost:*", "https://localhost:*", "http://127.0.0.1:*", "https://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
