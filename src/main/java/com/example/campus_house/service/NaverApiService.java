package com.example.campus_house.service;

// import com.example.campus_house.config.NaverApiConfig; // 현재 사용하지 않음
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverApiService {
    
    private final WebClient naverMapWebClient;
    private final WebClient naverSearchWebClient;
    // private final NaverApiConfig naverApiConfig; // 현재 사용하지 않음
    private final ObjectMapper objectMapper;
    
    /**
     * 주소를 좌표로 변환 (Geocoding)
     */
    public Mono<Map<String, Double>> getCoordinatesFromAddress(String address) {
        return naverMapWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode addresses = root.path("addresses");
                        
                        if (addresses.isArray() && addresses.size() > 0) {
                            JsonNode firstAddress = addresses.get(0);
                            double latitude = firstAddress.path("y").asDouble();
                            double longitude = firstAddress.path("x").asDouble();
                            
                            Map<String, Double> coordinates = new HashMap<>();
                            coordinates.put("latitude", latitude);
                            coordinates.put("longitude", longitude);
                            return coordinates;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse geocoding response", e);
                    }
                    return null;
                });
    }
    
    /**
     * 좌표를 주소로 변환 (Reverse Geocoding)
     */
    public Mono<String> getAddressFromCoordinates(double latitude, double longitude) {
        String coords = longitude + "," + latitude;
        
        return naverMapWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("coords", coords)
                        .queryParam("output", "json")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode addresses = root.path("results");
                        
                        if (addresses.isArray() && addresses.size() > 0) {
                            JsonNode firstAddress = addresses.get(0);
                            return firstAddress.path("region").path("area1").path("name").asText() + " " +
                                   firstAddress.path("region").path("area2").path("name").asText() + " " +
                                   firstAddress.path("region").path("area3").path("name").asText();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse reverse geocoding response", e);
                    }
                    return "주소를 찾을 수 없습니다.";
                });
    }
    
    /**
     * 지역 검색
     */
    public Mono<Map<String, Object>> searchPlaces(String query, int display) {
        return naverSearchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", query)
                        .queryParam("display", display)
                        .queryParam("sort", "comment")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        Map<String, Object> result = new HashMap<>();
                        result.put("items", root.path("items"));
                        result.put("total", root.path("total").asInt());
                        result.put("start", root.path("start").asInt());
                        result.put("display", root.path("display").asInt());
                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse search response", e);
                    }
                });
    }
    
    /**
     * 두 좌표 간의 거리 계산 (하버사인 공식)
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // km 단위
    }
}
