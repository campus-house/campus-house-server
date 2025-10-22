package com.example.campus_house.service;

import com.example.campus_house.entity.Building;
import com.example.campus_house.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 건물 지오코딩 서비스
 * 네이버 지오코딩 API를 활용하여 건물의 실제 좌표를 가져오고
 * 경희대학교 국제캠퍼스와 영통역까지의 도보 시간을 계산합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BuildingGeocodingService {
    
    private final BuildingRepository buildingRepository;
    private final NaverApiService naverApiService;
    
    // 경희대학교 국제캠퍼스 좌표
    private static final double KYUNGHEE_UNIV_LAT = 37.2636;
    private static final double KYUNGHEE_UNIV_LNG = 127.0286;
    
    // 영통역 좌표
    private static final double YEONGTONG_STATION_LAT = 37.2516;
    private static final double YEONGTONG_STATION_LNG = 127.0713;
    
    // 도보 속도 (km/h)
    private static final double WALKING_SPEED_KMH = 4.0;
    
    /**
     * 특정 건물의 좌표를 업데이트합니다.
     */
    @Transactional
    public Building updateBuildingCoordinates(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        try {
            // 네이버 API로 실제 좌표 조회
            Map<String, Double> coordinates = naverApiService.getCoordinatesFromAddress(building.getAddress())
                    .block(); // 동기 호출
            
            if (coordinates != null && coordinates.containsKey("latitude") && coordinates.containsKey("longitude")) {
                double latitude = coordinates.get("latitude");
                double longitude = coordinates.get("longitude");
                
                // 좌표 업데이트
                building.setLatitude(latitude);
                building.setLongitude(longitude);
                
                // 도보 시간 계산
                int schoolWalkingTime = calculateWalkingTimeToSchool(latitude, longitude);
                int stationWalkingTime = calculateWalkingTimeToStation(latitude, longitude);
                
                building.setSchoolWalkingTime(schoolWalkingTime);
                building.setStationWalkingTime(stationWalkingTime);
                
                Building updatedBuilding = buildingRepository.save(building);
                
                log.info("건물 ID {} 좌표 업데이트 완료 - 위도: {}, 경도: {}, 학교까지: {}분, 영통역까지: {}분", 
                    buildingId, latitude, longitude, schoolWalkingTime, stationWalkingTime);
                
                return updatedBuilding;
            } else {
                log.warn("건물 ID {}의 좌표를 찾을 수 없습니다. 주소: {}", buildingId, building.getAddress());
                return building;
            }
            
        } catch (Exception e) {
            log.error("건물 ID {} 좌표 업데이트 중 오류 발생", buildingId, e);
            throw new RuntimeException("좌표 업데이트 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 모든 건물의 좌표를 업데이트합니다.
     */
    @Transactional
    public int updateAllBuildingsCoordinates() {
        List<Building> buildings = buildingRepository.findAll();
        int updatedCount = 0;
        
        log.info("전체 {}개 건물의 좌표 업데이트 시작", buildings.size());
        
        for (Building building : buildings) {
            try {
                // API 호출 제한을 위한 대기 (초당 1회 제한)
                if (updatedCount > 0) {
                    TimeUnit.SECONDS.sleep(1);
                }
                
                updateBuildingCoordinates(building.getId());
                updatedCount++;
                
                log.info("진행률: {}/{} ({}%)", updatedCount, buildings.size(), 
                    (updatedCount * 100) / buildings.size());
                
            } catch (Exception e) {
                log.error("건물 ID {} 업데이트 중 오류 발생", building.getId(), e);
            }
        }
        
        log.info("건물 좌표 업데이트 완료 - 총 {}개 건물 업데이트됨", updatedCount);
        return updatedCount;
    }
    
    /**
     * 경희대학교 국제캠퍼스까지의 도보 시간을 계산합니다.
     */
    private int calculateWalkingTimeToSchool(double latitude, double longitude) {
        double distance = naverApiService.calculateDistance(
            latitude, longitude, KYUNGHEE_UNIV_LAT, KYUNGHEE_UNIV_LNG);
        
        // 도보 시간 계산 (분 단위)
        double walkingTimeHours = distance / WALKING_SPEED_KMH;
        int walkingTimeMinutes = (int) Math.round(walkingTimeHours * 60);
        
        // 최소 1분, 최대 60분으로 제한
        return Math.max(1, Math.min(60, walkingTimeMinutes));
    }
    
    /**
     * 영통역까지의 도보 시간을 계산합니다.
     */
    private int calculateWalkingTimeToStation(double latitude, double longitude) {
        double distance = naverApiService.calculateDistance(
            latitude, longitude, YEONGTONG_STATION_LAT, YEONGTONG_STATION_LNG);
        
        // 도보 시간 계산 (분 단위)
        double walkingTimeHours = distance / WALKING_SPEED_KMH;
        int walkingTimeMinutes = (int) Math.round(walkingTimeHours * 60);
        
        // 최소 1분, 최대 60분으로 제한
        return Math.max(1, Math.min(60, walkingTimeMinutes));
    }
    
    /**
     * 특정 건물의 도보 시간을 조회합니다.
     */
    public Map<String, Integer> getWalkingTimes(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        if (building.getLatitude() == null || building.getLongitude() == null) {
            throw new RuntimeException("건물의 좌표 정보가 없습니다.");
        }
        
        int schoolTime = calculateWalkingTimeToSchool(building.getLatitude(), building.getLongitude());
        int stationTime = calculateWalkingTimeToStation(building.getLatitude(), building.getLongitude());
        
        return Map.of(
            "schoolWalkingTime", schoolTime,
            "stationWalkingTime", stationTime
        );
    }
    
    /**
     * 좌표가 없는 건물들을 조회합니다.
     */
    public List<Building> getBuildingsWithoutCoordinates() {
        return buildingRepository.findByLatitudeIsNullOrLongitudeIsNull();
    }
    
    /**
     * 좌표가 있는 건물 수를 조회합니다.
     */
    public long getBuildingsWithCoordinatesCount() {
        return buildingRepository.countByLatitudeIsNotNullAndLongitudeIsNotNull();
    }
}
