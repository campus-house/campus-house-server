package com.example.campus_house.service;

import com.example.campus_house.entity.Facility;
import com.example.campus_house.repository.FacilityRepository;
import com.example.campus_house.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 주변 생활시설 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NearbyFacilityService {
    
    private final FacilityRepository facilityRepository;
    private final DistanceCalculator distanceCalculator;
    
    // 반경 상수 (km)
    private static final double DEFAULT_RADIUS_KM = 1.0; // 1km
    
    /**
     * 특정 위치 주변의 생활시설 개수를 반환합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @return 생활시설 개수 맵 (편의점, 마트, 병원)
     */
    public Map<String, Integer> getNearbyFacilityCounts(Double latitude, Double longitude) {
        return getNearbyFacilityCounts(latitude, longitude, DEFAULT_RADIUS_KM);
    }
    
    /**
     * 특정 위치 주변의 생활시설 개수를 반환합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @return 생활시설 개수 맵 (편의점, 마트, 병원)
     */
    public Map<String, Integer> getNearbyFacilityCounts(Double latitude, Double longitude, Double radiusKm) {
        Map<String, Integer> counts = new HashMap<>();
        
        try {
            // 편의점 개수
            Long convenienceStoreCount = facilityRepository.countNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, Facility.Category.CONVENIENCE_STORE.name());
            counts.put("convenienceStores", convenienceStoreCount.intValue());
            
            // 마트 개수
            Long martCount = facilityRepository.countNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, Facility.Category.MART.name());
            counts.put("marts", martCount.intValue());
            
            // 병원 개수
            Long hospitalCount = facilityRepository.countNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, Facility.Category.HOSPITAL.name());
            counts.put("hospitals", hospitalCount.intValue());
            
            log.info("주변 생활시설 개수 계산 완료 - 위도: {}, 경도: {}, 반경: {}km, 편의점: {}, 마트: {}, 병원: {}", 
                latitude, longitude, radiusKm, convenienceStoreCount, martCount, hospitalCount);
            
        } catch (Exception e) {
            log.error("주변 생활시설 개수 계산 중 오류 발생", e);
            // 오류 발생 시 기본값 반환
            counts.put("convenienceStores", 0);
            counts.put("marts", 0);
            counts.put("hospitals", 0);
        }
        
        return counts;
    }
    
    /**
     * 특정 위치 주변의 생활시설 목록을 반환합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @return 주변 생활시설 목록
     */
    public List<Facility> getNearbyFacilities(Double latitude, Double longitude, Double radiusKm) {
        try {
            return facilityRepository.findNearbyFacilities(latitude, longitude, radiusKm);
        } catch (Exception e) {
            log.error("주변 생활시설 조회 중 오류 발생", e);
            return List.of();
        }
    }
    
    /**
     * 특정 카테고리의 주변 생활시설 목록을 반환합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @param category 카테고리
     * @return 주변 생활시설 목록
     */
    public List<Facility> getNearbyFacilitiesByCategory(Double latitude, Double longitude, 
                                                       Double radiusKm, Facility.Category category) {
        try {
            return facilityRepository.findNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, category.name());
        } catch (Exception e) {
            log.error("주변 생활시설 조회 중 오류 발생 - 카테고리: {}", category, e);
            return List.of();
        }
    }
    
    /**
     * 특정 위치 주변의 편의점 개수를 반환합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @return 편의점 개수
     */
    public Integer getConvenienceStoreCount(Double latitude, Double longitude, Double radiusKm) {
        try {
            Long count = facilityRepository.countNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, Facility.Category.CONVENIENCE_STORE.name());
            return count.intValue();
        } catch (Exception e) {
            log.error("편의점 개수 조회 중 오류 발생", e);
            return 0;
        }
    }
    
    /**
     * 특정 위치 주변의 마트 개수를 반환합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @return 마트 개수
     */
    public Integer getMartCount(Double latitude, Double longitude, Double radiusKm) {
        try {
            Long count = facilityRepository.countNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, Facility.Category.MART.name());
            return count.intValue();
        } catch (Exception e) {
            log.error("마트 개수 조회 중 오류 발생", e);
            return 0;
        }
    }
    
    /**
     * 특정 위치 주변의 병원 개수를 반환합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @return 병원 개수
     */
    public Integer getHospitalCount(Double latitude, Double longitude, Double radiusKm) {
        try {
            Long count = facilityRepository.countNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, Facility.Category.HOSPITAL.name());
            return count.intValue();
        } catch (Exception e) {
            log.error("병원 개수 조회 중 오류 발생", e);
            return 0;
        }
    }
    
    /**
     * 모든 건물의 주변 생활시설 개수를 업데이트합니다.
     * 
     * @param buildingIds 업데이트할 건물 ID 목록 (null이면 모든 건물)
     */
    public void updateAllBuildingsNearbyFacilityCounts(List<Long> buildingIds) {
        // 이 메서드는 BuildingService에서 구현됩니다.
        // 여기서는 로그만 남깁니다.
        log.info("건물 주변 생활시설 개수 업데이트 요청 - 건물 수: {}", 
            buildingIds != null ? buildingIds.size() : "전체");
    }
}
