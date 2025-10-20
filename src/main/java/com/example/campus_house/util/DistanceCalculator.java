package com.example.campus_house.util;

import org.springframework.stereotype.Component;

/**
 * 거리 계산 유틸리티 클래스
 * 하버사인 공식을 사용하여 두 지점 간의 거리를 계산합니다.
 */
@Component
public class DistanceCalculator {
    
    // 지구 반지름 (km)
    private static final double EARTH_RADIUS = 6371.0;
    
    /**
     * 하버사인 공식을 사용하여 두 지점 간의 거리를 계산합니다.
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 간의 거리 (km)
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 위도와 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // 위도와 경도의 차이
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        
        // 하버사인 공식
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * 두 지점이 지정된 반경 내에 있는지 확인합니다.
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @param radiusKm 반경 (km)
     * @return 반경 내에 있으면 true, 그렇지 않으면 false
     */
    public boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= radiusKm;
    }
    
    /**
     * 두 지점 간의 거리를 미터 단위로 반환합니다.
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 간의 거리 (m)
     */
    public double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        return calculateDistance(lat1, lon1, lat2, lon2) * 1000;
    }
    
    /**
     * 두 지점 간의 거리를 분 단위로 반환합니다 (평균 보행 속도 4km/h 기준).
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 간의 도보 시간 (분)
     */
    public double calculateWalkingTimeInMinutes(double lat1, double lon1, double lat2, double lon2) {
        double distanceKm = calculateDistance(lat1, lon1, lat2, lon2);
        double walkingSpeedKmh = 4.0; // 평균 보행 속도 4km/h
        return (distanceKm / walkingSpeedKmh) * 60; // 분 단위로 변환
    }
}
