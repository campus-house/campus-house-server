package com.example.campus_house.service;

import com.example.campus_house.entity.Building;
import com.example.campus_house.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingService {
    
    private final BuildingRepository buildingRepository;
    
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
    
    // 건물명으로 검색
    public Page<Building> searchBuildingsByName(String buildingName, Pageable pageable) {
        return buildingRepository.findByBuildingNameContaining(buildingName, pageable);
    }
    
    // 주소로 검색
    public Page<Building> searchBuildingsByAddress(String address, Pageable pageable) {
        return buildingRepository.findByAddressContaining(address, pageable);
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
    
    // 주차장 필터
    public Page<Building> searchBuildingsByParking(Boolean parkingRequired, Pageable pageable) {
        if (parkingRequired != null && parkingRequired) {
            return buildingRepository.findByParkingSpacesGreaterThan(0, pageable);
        }
        return buildingRepository.findAll(pageable);
    }
    
    // 엘리베이터 필터
    public Page<Building> searchBuildingsByElevator(Boolean elevatorRequired, Pageable pageable) {
        if (elevatorRequired != null && elevatorRequired) {
            return buildingRepository.findByElevatorsGreaterThan(0, pageable);
        }
        return buildingRepository.findAll(pageable);
    }
    
    // 학교 접근성 필터
    public Page<Building> searchBuildingsBySchoolAccessibility(Integer maxWalkingTime, Pageable pageable) {
        if (maxWalkingTime != null) {
            return buildingRepository.findBySchoolWalkingTimeLessThanEqual(maxWalkingTime, pageable);
        }
        return buildingRepository.findAll(pageable);
    }
    
    // 복합 필터링
    public Page<Building> searchBuildingsWithFilters(BigDecimal minDeposit, BigDecimal maxDeposit,
                                                    BigDecimal minMonthlyRent, BigDecimal maxMonthlyRent,
                                                    BigDecimal minJeonse, BigDecimal maxJeonse,
                                                    Boolean parkingRequired, Boolean elevatorRequired,
                                                    Integer maxWalkingTime, Pageable pageable) {
        return buildingRepository.findByFilters(minDeposit, maxDeposit, minMonthlyRent, maxMonthlyRent,
                                              minJeonse, maxJeonse, parkingRequired, elevatorRequired,
                                              maxWalkingTime, pageable);
    }
    
    // 최근 등록된 건물 조회
    public Page<Building> getRecentBuildings(Pageable pageable) {
        return buildingRepository.findByOrderByCreatedAtDesc(pageable);
    }
    
    // 인기 건물 조회 (스크랩 수 기준)
    public Page<Building> getPopularBuildings(Pageable pageable) {
        return buildingRepository.findByOrderByScrapCountDesc(pageable);
    }
    
    // 영통역 접근성 필터
    public Page<Building> searchBuildingsByStationAccessibility(Integer maxWalkingTime, Pageable pageable) {
        if (maxWalkingTime != null) {
            return buildingRepository.findByStationWalkingTimeLessThanEqual(maxWalkingTime, pageable);
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
        if (updatedBuilding.getParkingSpaces() != null) {
            building.setParkingSpaces(updatedBuilding.getParkingSpaces());
        }
        if (updatedBuilding.getElevators() != null) {
            building.setElevators(updatedBuilding.getElevators());
        }
        if (updatedBuilding.getBuildingUsage() != null) {
            building.setBuildingUsage(updatedBuilding.getBuildingUsage());
        }
        if (updatedBuilding.getNearbyFacilities() != null) {
            building.setNearbyFacilities(updatedBuilding.getNearbyFacilities());
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
}
