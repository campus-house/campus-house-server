package com.example.campus_house.controller;

import com.example.campus_house.entity.Facility;
import com.example.campus_house.repository.FacilityRepository;
import com.example.campus_house.service.NearbyFacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 생활시설 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "생활시설", description = "생활시설 관련 API")
public class FacilityController {
    
    private final FacilityRepository facilityRepository;
    private final NearbyFacilityService nearbyFacilityService;
    
    // ========== 기본 CRUD API ==========
    
    /**
     * 모든 생활시설을 조회합니다.
     * 
     * @param category 카테고리 (선택사항)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 생활시설 목록
     */
    @GetMapping
    @Operation(summary = "생활시설 목록 조회", description = "모든 생활시설을 조회합니다.")
    public ResponseEntity<Page<Facility>> getAllFacilities(
            @Parameter(description = "카테고리", example = "HOSPITAL") @RequestParam(required = false) String category,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Facility> facilities;
            
            if (category != null && !category.isEmpty()) {
                facilities = facilityRepository.findByCategory(category, pageable);
            } else {
                facilities = facilityRepository.findAll(pageable);
            }
            
            return ResponseEntity.ok(facilities);
        } catch (Exception e) {
            log.error("생활시설 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 생활시설을 조회합니다.
     * 
     * @param id 생활시설 ID
     * @return 생활시설 정보
     */
    @GetMapping("/{id}")
    @Operation(summary = "생활시설 상세 조회", description = "특정 생활시설의 상세 정보를 조회합니다.")
    public ResponseEntity<Facility> getFacility(@PathVariable Long id) {
        try {
            return facilityRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("생활시설 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 위치 주변의 생활시설 개수를 조회합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km, 기본값: 1km)
     * @return 주변 생활시설 개수
     */
    @GetMapping("/nearby/counts")
    @Operation(summary = "주변 생활시설 개수 조회", description = "특정 위치 주변의 편의점, 마트, 병원 개수를 조회합니다.")
    public ResponseEntity<Map<String, Integer>> getNearbyFacilityCounts(
            @Parameter(description = "위도", example = "37.2636") @RequestParam Double latitude,
            @Parameter(description = "경도", example = "127.0286") @RequestParam Double longitude,
            @Parameter(description = "반경 (km)", example = "1.0") @RequestParam(defaultValue = "1.0") Double radiusKm) {
        
        try {
            Map<String, Integer> counts = nearbyFacilityService.getNearbyFacilityCounts(latitude, longitude, radiusKm);
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            log.error("주변 생활시설 개수 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 위치 주변의 생활시설 목록을 조회합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km, 기본값: 1km)
     * @return 주변 생활시설 목록
     */
    @GetMapping("/nearby")
    @Operation(summary = "주변 생활시설 목록 조회", description = "특정 위치 주변의 생활시설 목록을 조회합니다.")
    public ResponseEntity<List<Facility>> getNearbyFacilities(
            @Parameter(description = "위도", example = "37.2636") @RequestParam Double latitude,
            @Parameter(description = "경도", example = "127.0286") @RequestParam Double longitude,
            @Parameter(description = "반경 (km)", example = "1.0") @RequestParam(defaultValue = "1.0") Double radiusKm) {
        
        try {
            List<Facility> facilities = nearbyFacilityService.getNearbyFacilities(latitude, longitude, radiusKm);
            return ResponseEntity.ok(facilities);
        } catch (Exception e) {
            log.error("주변 생활시설 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 카테고리의 주변 생활시설 목록을 조회합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km, 기본값: 1km)
     * @param category 카테고리 (CONVENIENCE_STORE, MART, HOSPITAL)
     * @return 주변 생활시설 목록
     */
    @GetMapping("/nearby/category")
    @Operation(summary = "카테고리별 주변 생활시설 조회", description = "특정 카테고리의 주변 생활시설 목록을 조회합니다.")
    public ResponseEntity<List<Facility>> getNearbyFacilitiesByCategory(
            @Parameter(description = "위도", example = "37.2636") @RequestParam Double latitude,
            @Parameter(description = "경도", example = "127.0286") @RequestParam Double longitude,
            @Parameter(description = "반경 (km)", example = "1.0") @RequestParam(defaultValue = "1.0") Double radiusKm,
            @Parameter(description = "카테고리", example = "CONVENIENCE_STORE") @RequestParam String category) {
        
        try {
            Facility.Category facilityCategory = Facility.Category.valueOf(category.toUpperCase());
            List<Facility> facilities = nearbyFacilityService.getNearbyFacilitiesByCategory(
                latitude, longitude, radiusKm, facilityCategory);
            return ResponseEntity.ok(facilities);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 카테고리: {}", category);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("카테고리별 주변 생활시설 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 편의점 개수를 조회합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km, 기본값: 1km)
     * @return 편의점 개수
     */
    @GetMapping("/nearby/convenience-stores/count")
    @Operation(summary = "주변 편의점 개수 조회", description = "특정 위치 주변의 편의점 개수를 조회합니다.")
    public ResponseEntity<Integer> getConvenienceStoreCount(
            @Parameter(description = "위도", example = "37.2636") @RequestParam Double latitude,
            @Parameter(description = "경도", example = "127.0286") @RequestParam Double longitude,
            @Parameter(description = "반경 (km)", example = "1.0") @RequestParam(defaultValue = "1.0") Double radiusKm) {
        
        try {
            Integer count = nearbyFacilityService.getConvenienceStoreCount(latitude, longitude, radiusKm);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("편의점 개수 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 마트 개수를 조회합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km, 기본값: 1km)
     * @return 마트 개수
     */
    @GetMapping("/nearby/marts/count")
    @Operation(summary = "주변 마트 개수 조회", description = "특정 위치 주변의 마트 개수를 조회합니다.")
    public ResponseEntity<Integer> getMartCount(
            @Parameter(description = "위도", example = "37.2636") @RequestParam Double latitude,
            @Parameter(description = "경도", example = "127.0286") @RequestParam Double longitude,
            @Parameter(description = "반경 (km)", example = "1.0") @RequestParam(defaultValue = "1.0") Double radiusKm) {
        
        try {
            Integer count = nearbyFacilityService.getMartCount(latitude, longitude, radiusKm);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("마트 개수 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 병원 개수를 조회합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km, 기본값: 1km)
     * @return 병원 개수
     */
    @GetMapping("/nearby/hospitals/count")
    @Operation(summary = "주변 병원 개수 조회", description = "특정 위치 주변의 병원 개수를 조회합니다.")
    public ResponseEntity<Integer> getHospitalCount(
            @Parameter(description = "위도", example = "37.2636") @RequestParam Double latitude,
            @Parameter(description = "경도", example = "127.0286") @RequestParam Double longitude,
            @Parameter(description = "반경 (km)", example = "1.0") @RequestParam(defaultValue = "1.0") Double radiusKm) {
        
        try {
            Integer count = nearbyFacilityService.getHospitalCount(latitude, longitude, radiusKm);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("병원 개수 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
