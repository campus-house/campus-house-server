package com.example.campus_house.controller;

import com.example.campus_house.entity.Building;
import com.example.campus_house.entity.BuildingScrap;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.BuildingService;
import com.example.campus_house.service.BuildingScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {
    
    private final BuildingService buildingService;
    private final BuildingScrapService buildingScrapService;
    private final AuthService authService;
    
    // 모든 건물 조회
    @GetMapping
    public ResponseEntity<Page<Building>> getAllBuildings(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.getAllBuildings(pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 건물 상세 조회
    @GetMapping("/{buildingId}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long buildingId) {
        Optional<Building> building = buildingService.getBuildingById(buildingId);
        if (building.isPresent()) {
            return ResponseEntity.ok(building.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    // 키워드로 건물 검색
    @GetMapping("/search")
    public ResponseEntity<Page<Building>> searchBuildingsByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByKeyword(keyword, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 건물명으로 검색
    @GetMapping("/search/building")
    public ResponseEntity<Page<Building>> searchBuildingsByName(
            @RequestParam String buildingName,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByName(buildingName, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 주소로 검색
    @GetMapping("/search/address")
    public ResponseEntity<Page<Building>> searchBuildingsByAddress(
            @RequestParam String address,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByAddress(address, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 위치 기반 검색
    @GetMapping("/nearby")
    public ResponseEntity<Page<Building>> searchBuildingsByLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "1.0") Double radiusKm,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByLocation(latitude, longitude, radiusKm, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 건물 정보 필터링
    @GetMapping("/search/filters")
    public ResponseEntity<Page<Building>> searchBuildingsWithFilters(
            @RequestParam(required = false) BigDecimal minDeposit,
            @RequestParam(required = false) BigDecimal maxDeposit,
            @RequestParam(required = false) BigDecimal minMonthlyRent,
            @RequestParam(required = false) BigDecimal maxMonthlyRent,
            @RequestParam(required = false) BigDecimal minJeonse,
            @RequestParam(required = false) BigDecimal maxJeonse,
            @RequestParam(required = false) Boolean parkingRequired,
            @RequestParam(required = false) Boolean elevatorRequired,
            @RequestParam(required = false) Integer maxWalkingTime,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        Page<Building> buildings = buildingService.searchBuildingsWithFilters(
                minDeposit, maxDeposit, minMonthlyRent, maxMonthlyRent,
                minJeonse, maxJeonse, parkingRequired, elevatorRequired,
                maxWalkingTime, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 보증금 범위 필터
    @GetMapping("/search/filters/deposit")
    public ResponseEntity<Page<Building>> searchBuildingsByDepositRange(
            @RequestParam BigDecimal minDeposit,
            @RequestParam BigDecimal maxDeposit,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByDepositRange(minDeposit, maxDeposit, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 월세 범위 필터
    @GetMapping("/search/filters/monthly-rent")
    public ResponseEntity<Page<Building>> searchBuildingsByMonthlyRentRange(
            @RequestParam BigDecimal minMonthlyRent,
            @RequestParam BigDecimal maxMonthlyRent,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByMonthlyRentRange(minMonthlyRent, maxMonthlyRent, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 전세 범위 필터
    @GetMapping("/search/filters/jeonse")
    public ResponseEntity<Page<Building>> searchBuildingsByJeonseRange(
            @RequestParam BigDecimal minJeonse,
            @RequestParam BigDecimal maxJeonse,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByJeonseRange(minJeonse, maxJeonse, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 주차장 필터
    @GetMapping("/search/filters/parking")
    public ResponseEntity<Page<Building>> searchBuildingsByParking(
            @RequestParam Boolean parkingRequired,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByParking(parkingRequired, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 엘리베이터 필터
    @GetMapping("/search/filters/elevator")
    public ResponseEntity<Page<Building>> searchBuildingsByElevator(
            @RequestParam Boolean elevatorRequired,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByElevator(elevatorRequired, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 학교 접근성 필터
    @GetMapping("/search/filters/school-accessibility")
    public ResponseEntity<Page<Building>> searchBuildingsBySchoolAccessibility(
            @RequestParam Integer maxWalkingTime,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsBySchoolAccessibility(maxWalkingTime, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 최근 등록된 건물 조회
    @GetMapping("/recent")
    public ResponseEntity<Page<Building>> getRecentBuildings(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.getRecentBuildings(pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 인기 건물 조회 (스크랩 수 기준)
    @GetMapping("/popular")
    public ResponseEntity<Page<Building>> getPopularBuildings(
            @PageableDefault(size = 20, sort = "scrapCount", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.getPopularBuildings(pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 영통역 접근성 필터
    @GetMapping("/search/filters/station-accessibility")
    public ResponseEntity<Page<Building>> searchBuildingsByStationAccessibility(
            @RequestParam Integer maxWalkingTime,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByStationAccessibility(maxWalkingTime, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 건물 생성
    @PostMapping
    public ResponseEntity<Building> createBuilding(@RequestBody Building building) {
        Building createdBuilding = buildingService.createBuilding(building);
        return ResponseEntity.ok(createdBuilding);
    }
    
    // 건물 수정
    @PutMapping("/{buildingId}")
    public ResponseEntity<Building> updateBuilding(@PathVariable Long buildingId, @RequestBody Building building) {
        try {
            Building updatedBuilding = buildingService.updateBuilding(buildingId, building);
            return ResponseEntity.ok(updatedBuilding);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 건물 삭제
    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long buildingId) {
        try {
            buildingService.deleteBuilding(buildingId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 네이버 API 연동 - 주소를 좌표로 변환
    @GetMapping("/geocode")
    public ResponseEntity<?> geocodeAddress(@RequestParam String address) {
        // TODO: 네이버 지도 API 연동 구현
        return ResponseEntity.ok().build();
    }
    
    // 네이버 API 연동 - 좌표를 주소로 변환
    @GetMapping("/reverse-geocode")
    public ResponseEntity<?> reverseGeocode(@RequestParam Double latitude, @RequestParam Double longitude) {
        // TODO: 네이버 지도 API 연동 구현
        return ResponseEntity.ok().build();
    }
    
    // 네이버 API 연동 - 장소 검색
    @GetMapping("/search/places")
    public ResponseEntity<?> searchPlaces(@RequestParam String query, @RequestParam(defaultValue = "10") Integer display) {
        // TODO: 네이버 지도 API 연동 구현
        return ResponseEntity.ok().build();
    }
    
    // 학교까지 걸리는 시간 계산
    @GetMapping("/{buildingId}/school-walking-time")
    public ResponseEntity<?> getSchoolWalkingTime(@PathVariable Long buildingId, @RequestParam Long schoolId) {
        // TODO: 지도 API를 통한 길찾기 구현
        return ResponseEntity.ok().build();
    }
    
    // 건물 스크랩
    @PostMapping("/{buildingId}/scrap")
    public ResponseEntity<BuildingScrap> scrapBuilding(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            BuildingScrap scrap = buildingScrapService.scrapBuilding(user.getId(), buildingId);
            return ResponseEntity.ok(scrap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 건물 스크랩 취소
    @DeleteMapping("/{buildingId}/scrap")
    public ResponseEntity<Void> unscrapBuilding(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            buildingScrapService.unscrapBuilding(user.getId(), buildingId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 건물 스크랩 여부 확인
    @GetMapping("/{buildingId}/scrap/status")
    public ResponseEntity<Boolean> getScrapStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            boolean isScraped = buildingScrapService.isScraped(user.getId(), buildingId);
            return ResponseEntity.ok(isScraped);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
