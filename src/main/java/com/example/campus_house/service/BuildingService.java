package com.example.campus_house.service;

import com.example.campus_house.entity.Building;
import com.example.campus_house.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BuildingService {
    
    private final BuildingRepository buildingRepository;
    private final NearbyFacilityService nearbyFacilityService;
    
    // 모든 건물 조회
    public Page<Building> getAllBuildings(Pageable pageable) {
        return buildingRepository.findAll(pageable);
    }
    
    // 건물 ID로 조회
    public Optional<Building> getBuildingById(Long buildingId) {
        return buildingRepository.findById(buildingId);
    }
    
    // 키워드로 건물 검색
    public Page<Building> searchBuildingsByKeyword(String keyword, Pageable pageable) {
        return buildingRepository.findByKeyword(keyword, pageable);
    }
    
    
    // 위치 기반 검색
    public Page<Building> searchBuildingsByLocation(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        return buildingRepository.findByLocationWithinRadius(latitude, longitude, radiusKm, pageable);
    }
    
    // 보증금 범위로 검색
    public Page<Building> searchBuildingsByDepositRange(BigDecimal minDeposit, BigDecimal maxDeposit, Pageable pageable) {
        return buildingRepository.findByDepositBetween(minDeposit, maxDeposit, pageable);
    }
    
    // 월세 범위로 검색
    public Page<Building> searchBuildingsByMonthlyRentRange(BigDecimal minMonthlyRent, BigDecimal maxMonthlyRent, Pageable pageable) {
        return buildingRepository.findByMonthlyRentBetween(minMonthlyRent, maxMonthlyRent, pageable);
    }
    
    // 전세 범위로 검색
    public Page<Building> searchBuildingsByJeonseRange(BigDecimal minJeonse, BigDecimal maxJeonse, Pageable pageable) {
        return buildingRepository.findByJeonseBetween(minJeonse, maxJeonse, pageable);
    }
    
    
    // 엘리베이터 필터
    public Page<Building> searchBuildingsByElevator(Boolean elevatorRequired, Pageable pageable) {
        if (elevatorRequired != null && elevatorRequired) {
            return buildingRepository.findByElevatorsGreaterThan(0, pageable);
        }
        return buildingRepository.findAll(pageable);
    }
    
    
    // 복합 필터링
    public Page<Building> searchBuildingsWithFilters(BigDecimal minDeposit, BigDecimal maxDeposit,
                                                    BigDecimal minMonthlyRent, BigDecimal maxMonthlyRent,
                                                    BigDecimal minJeonse, BigDecimal maxJeonse,
                                                    Boolean elevatorRequired,
                                                    Integer maxWalkingTime, String buildingUsage, Pageable pageable) {
        return buildingRepository.findByFilters(minDeposit, maxDeposit, minMonthlyRent, maxMonthlyRent,
                                              minJeonse, maxJeonse, elevatorRequired,
                                              maxWalkingTime, buildingUsage, pageable);
    }
    
    
    
    // 건물 용도별 필터
    public Page<Building> searchBuildingsByBuildingUsage(String buildingUsage, Pageable pageable) {
        return buildingRepository.findByBuildingUsageContaining(buildingUsage, pageable);
    }
    
    // 건물 용도 목록 조회
    public java.util.List<String> getDistinctBuildingUsages() {
        return buildingRepository.findDistinctBuildingUsages();
    }
    
    // 편의점 개수로 검색 (외부 API 연동 예정)
    public Page<Building> searchBuildingsByConvenienceStores(Integer minConvenienceStores, Pageable pageable) {
        if (minConvenienceStores != null) {
            return buildingRepository.findByNearbyConvenienceStoresGreaterThanEqual(minConvenienceStores, pageable);
        }
        return buildingRepository.findAll(pageable);
    }
    
    // 마트 개수로 검색 (외부 API 연동 예정)
    public Page<Building> searchBuildingsByMarts(Integer minMarts, Pageable pageable) {
        if (minMarts != null) {
            return buildingRepository.findByNearbyMartsGreaterThanEqual(minMarts, pageable);
        }
        return buildingRepository.findAll(pageable);
    }
    
    // 병원 개수로 검색 (외부 API 연동 예정)
    public Page<Building> searchBuildingsByHospitals(Integer minHospitals, Pageable pageable) {
        if (minHospitals != null) {
            return buildingRepository.findByNearbyHospitalsGreaterThanEqual(minHospitals, pageable);
        }
        return buildingRepository.findAll(pageable);
    }
    
    // 건물 생성
    @Transactional
    public Building createBuilding(Building building) {
        return buildingRepository.save(building);
    }
    
    // 건물 수정
    @Transactional
    public Building updateBuilding(Long buildingId, Building updatedBuilding) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        // 필드 업데이트
        if (updatedBuilding.getBuildingName() != null) {
            building.setBuildingName(updatedBuilding.getBuildingName());
        }
        if (updatedBuilding.getAddress() != null) {
            building.setAddress(updatedBuilding.getAddress());
        }
        if (updatedBuilding.getDeposit() != null) {
            building.setDeposit(updatedBuilding.getDeposit());
        }
        if (updatedBuilding.getMonthlyRent() != null) {
            building.setMonthlyRent(updatedBuilding.getMonthlyRent());
        }
        if (updatedBuilding.getJeonse() != null) {
            building.setJeonse(updatedBuilding.getJeonse());
        }
        if (updatedBuilding.getHouseholds() != null) {
            building.setHouseholds(updatedBuilding.getHouseholds());
        }
        if (updatedBuilding.getHeatingType() != null) {
            building.setHeatingType(updatedBuilding.getHeatingType());
        }
        if (updatedBuilding.getElevators() != null) {
            building.setElevators(updatedBuilding.getElevators());
        }
        if (updatedBuilding.getBuildingUsage() != null) {
            building.setBuildingUsage(updatedBuilding.getBuildingUsage());
        }
        if (updatedBuilding.getNearbyConvenienceStores() != null) {
            building.setNearbyConvenienceStores(updatedBuilding.getNearbyConvenienceStores());
        }
        if (updatedBuilding.getNearbyMarts() != null) {
            building.setNearbyMarts(updatedBuilding.getNearbyMarts());
        }
        if (updatedBuilding.getNearbyHospitals() != null) {
            building.setNearbyHospitals(updatedBuilding.getNearbyHospitals());
        }
        if (updatedBuilding.getSchoolWalkingTime() != null) {
            building.setSchoolWalkingTime(updatedBuilding.getSchoolWalkingTime());
        }
        
        return buildingRepository.save(building);
    }
    
    // 건물 삭제
    @Transactional
    public void deleteBuilding(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new RuntimeException("건물을 찾을 수 없습니다.");
        }
        buildingRepository.deleteById(buildingId);
    }
    
    
    // 스크랩 수 증가
    @Transactional
    public void incrementScrapCount(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        building.setScrapCount(building.getScrapCount() != null ? building.getScrapCount() + 1 : 1);
        buildingRepository.save(building);
    }
    
    // 스크랩 수 감소
    @Transactional
    public void decrementScrapCount(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        if (building.getScrapCount() != null && building.getScrapCount() > 0) {
            building.setScrapCount(building.getScrapCount() - 1);
            buildingRepository.save(building);
        }
    }
    
    // ========== 주변 생활시설 관련 메서드 ==========
    
    /**
     * 특정 건물의 주변 생활시설 개수를 업데이트합니다.
     * 
     * @param buildingId 건물 ID
     * @return 업데이트된 건물 정보
     */
    @Transactional
    public Building updateNearbyFacilityCounts(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        if (building.getLatitude() == null || building.getLongitude() == null) {
            log.warn("건물 ID {}의 위도/경도 정보가 없어 주변 생활시설 개수를 업데이트할 수 없습니다.", buildingId);
            return building;
        }
        
        try {
            Map<String, Integer> facilityCounts = nearbyFacilityService.getNearbyFacilityCounts(
                building.getLatitude(), building.getLongitude());
            
            building.setNearbyConvenienceStores(facilityCounts.get("convenienceStores"));
            building.setNearbyMarts(facilityCounts.get("marts"));
            building.setNearbyHospitals(facilityCounts.get("hospitals"));
            
            Building updatedBuilding = buildingRepository.save(building);
            
            log.info("건물 ID {}의 주변 생활시설 개수 업데이트 완료 - 편의점: {}, 마트: {}, 병원: {}", 
                buildingId, facilityCounts.get("convenienceStores"), 
                facilityCounts.get("marts"), facilityCounts.get("hospitals"));
            
            return updatedBuilding;
            
        } catch (Exception e) {
            log.error("건물 ID {}의 주변 생활시설 개수 업데이트 중 오류 발생", buildingId, e);
            throw new RuntimeException("주변 생활시설 개수 업데이트 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 모든 건물의 주변 생활시설 개수를 업데이트합니다.
     * 
     * @return 업데이트된 건물 수
     */
    @Transactional
    public int updateAllBuildingsNearbyFacilityCounts() {
        List<Building> buildings = buildingRepository.findAll();
        int updatedCount = 0;
        
        log.info("전체 {}개 건물의 주변 생활시설 개수 업데이트 시작", buildings.size());
        
        for (Building building : buildings) {
            try {
                if (building.getLatitude() != null && building.getLongitude() != null) {
                    updateNearbyFacilityCounts(building.getId());
                    updatedCount++;
                } else {
                    log.warn("건물 ID {}의 위도/경도 정보가 없어 건너뜁니다.", building.getId());
                }
            } catch (Exception e) {
                log.error("건물 ID {} 업데이트 중 오류 발생", building.getId(), e);
            }
        }
        
        log.info("주변 생활시설 개수 업데이트 완료 - 총 {}개 건물 업데이트됨", updatedCount);
        return updatedCount;
    }
    
    /**
     * 특정 건물들의 주변 생활시설 개수를 업데이트합니다.
     * 
     * @param buildingIds 업데이트할 건물 ID 목록
     * @return 업데이트된 건물 수
     */
    @Transactional
    public int updateBuildingsNearbyFacilityCounts(List<Long> buildingIds) {
        int updatedCount = 0;
        
        log.info("{}개 건물의 주변 생활시설 개수 업데이트 시작", buildingIds.size());
        
        for (Long buildingId : buildingIds) {
            try {
                updateNearbyFacilityCounts(buildingId);
                updatedCount++;
            } catch (Exception e) {
                log.error("건물 ID {} 업데이트 중 오류 발생", buildingId, e);
            }
        }
        
        log.info("주변 생활시설 개수 업데이트 완료 - 총 {}개 건물 업데이트됨", updatedCount);
        return updatedCount;
    }
    
    /**
     * 특정 건물의 주변 생활시설 개수를 조회합니다.
     * 
     * @param buildingId 건물 ID
     * @return 주변 생활시설 개수 맵
     */
    public Map<String, Integer> getNearbyFacilityCounts(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        if (building.getLatitude() == null || building.getLongitude() == null) {
            throw new RuntimeException("건물의 위도/경도 정보가 없습니다.");
        }
        
        return nearbyFacilityService.getNearbyFacilityCounts(
            building.getLatitude(), building.getLongitude());
    }
    
    /**
     * 포맷팅된 건물 정보를 반환합니다.
     * 금액은 정수로 변환하고 약식 표기를 적용합니다.
     * 
     * @param building 원본 건물 정보
     * @return 포맷팅된 건물 정보
     */
    public Building getFormattedBuilding(Building building) {
        if (building == null) return null;
        
        // 소수점 제거하고 정수로 변환
        if (building.getDeposit() != null) {
            building.setDeposit(building.getDeposit().setScale(0, java.math.RoundingMode.DOWN));
        }
        if (building.getMonthlyRent() != null) {
            building.setMonthlyRent(building.getMonthlyRent().setScale(0, java.math.RoundingMode.DOWN));
        }
        if (building.getJeonse() != null) {
            building.setJeonse(building.getJeonse().setScale(0, java.math.RoundingMode.DOWN));
        }
        
        return building;
    }
    
    /**
     * 포맷팅된 건물 목록을 반환합니다.
     * 
     * @param buildings 원본 건물 목록
     * @return 포맷팅된 건물 목록
     */
    public Page<Building> getFormattedBuildings(Page<Building> buildings) {
        if (buildings == null || buildings.getContent().isEmpty()) {
            return buildings;
        }
        
        // 각 건물에 대해 포맷팅 적용
        buildings.getContent().forEach(this::getFormattedBuilding);
        
        return buildings;
    }
}
