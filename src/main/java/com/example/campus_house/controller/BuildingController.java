package com.example.campus_house.controller;

import com.example.campus_house.entity.Building;
import com.example.campus_house.entity.BuildingScrap;
import com.example.campus_house.entity.BuildingReview;
import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.dto.QuestionRequest;
import com.example.campus_house.dto.BuildingReviewStatsDto;
import com.example.campus_house.dto.BuildingRatingStatsDto;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.BuildingService;
import com.example.campus_house.service.BuildingScrapService;
import com.example.campus_house.service.BuildingReviewService;
import com.example.campus_house.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {
    
    private final BuildingService buildingService;
    private final BuildingScrapService buildingScrapService;
    private final BuildingReviewService buildingReviewService;
    private final PostService postService;
    private final AuthService authService;
    
    // 모든 건물 조회
    @GetMapping
    public ResponseEntity<Page<Building>> getAllBuildings(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.getAllBuildings(pageable);
        Page<Building> formattedBuildings = buildingService.getFormattedBuildings(buildings);
        return ResponseEntity.ok(formattedBuildings);
    }
    
    
    // 키워드로 건물 검색
    @GetMapping("/search")
    public ResponseEntity<Page<Building>> searchBuildingsByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByKeyword(keyword, pageable);
        Page<Building> formattedBuildings = buildingService.getFormattedBuildings(buildings);
        return ResponseEntity.ok(formattedBuildings);
    }
    
    
    // 위치 기반 검색
    @GetMapping("/nearby")
    public ResponseEntity<Page<Building>> searchBuildingsByLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "1.0") Double radiusKm,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByLocation(latitude, longitude, radiusKm, pageable);
        Page<Building> formattedBuildings = buildingService.getFormattedBuildings(buildings);
        return ResponseEntity.ok(formattedBuildings);
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
            @RequestParam(required = false) Boolean elevatorRequired,
            @RequestParam(required = false) Integer maxWalkingTime,
            @RequestParam(required = false) String buildingUsage,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        Page<Building> buildings = buildingService.searchBuildingsWithFilters(
                minDeposit, maxDeposit, minMonthlyRent, maxMonthlyRent,
                minJeonse, maxJeonse, elevatorRequired,
                maxWalkingTime, buildingUsage, pageable);
        Page<Building> formattedBuildings = buildingService.getFormattedBuildings(buildings);
        return ResponseEntity.ok(formattedBuildings);
    }
    
    // 보증금 범위 필터
    @GetMapping("/search/filters/deposit")
    public ResponseEntity<Page<Building>> searchBuildingsByDepositRange(
            @RequestParam BigDecimal minDeposit,
            @RequestParam BigDecimal maxDeposit,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByDepositRange(minDeposit, maxDeposit, pageable);
        Page<Building> formattedBuildings = buildingService.getFormattedBuildings(buildings);
        return ResponseEntity.ok(formattedBuildings);
    }
    
    // 월세 범위 필터
    @GetMapping("/search/filters/monthly-rent")
    public ResponseEntity<Page<Building>> searchBuildingsByMonthlyRentRange(
            @RequestParam BigDecimal minMonthlyRent,
            @RequestParam BigDecimal maxMonthlyRent,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByMonthlyRentRange(minMonthlyRent, maxMonthlyRent, pageable);
        Page<Building> formattedBuildings = buildingService.getFormattedBuildings(buildings);
        return ResponseEntity.ok(formattedBuildings);
    }
    
    // 전세 범위 필터
    @GetMapping("/search/filters/jeonse")
    public ResponseEntity<Page<Building>> searchBuildingsByJeonseRange(
            @RequestParam BigDecimal minJeonse,
            @RequestParam BigDecimal maxJeonse,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByJeonseRange(minJeonse, maxJeonse, pageable);
        Page<Building> formattedBuildings = buildingService.getFormattedBuildings(buildings);
        return ResponseEntity.ok(formattedBuildings);
    }
    
    
    // 엘리베이터 필터
    @GetMapping("/search/filters/elevator")
    public ResponseEntity<Page<Building>> searchBuildingsByElevator(
            @RequestParam Boolean elevatorRequired,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByElevator(elevatorRequired, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    
    
    
    // 건물 용도별 필터 (오피스텔, 아파트, 원룸 등)
    @GetMapping("/search/filters/building-usage")
    public ResponseEntity<Page<Building>> searchBuildingsByBuildingUsage(
            @RequestParam String buildingUsage,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByBuildingUsage(buildingUsage, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 건물 용도 목록 조회
    @GetMapping("/building-usages")
    public ResponseEntity<?> getBuildingUsages() {
        return ResponseEntity.ok(buildingService.getDistinctBuildingUsages());
    }
    
    // 편의점 개수로 검색 (외부 API 연동 예정)
    @GetMapping("/search/filters/convenience-stores")
    public ResponseEntity<Page<Building>> searchBuildingsByConvenienceStores(
            @RequestParam Integer minConvenienceStores,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByConvenienceStores(minConvenienceStores, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 마트 개수로 검색 (외부 API 연동 예정)
    @GetMapping("/search/filters/marts")
    public ResponseEntity<Page<Building>> searchBuildingsByMarts(
            @RequestParam Integer minMarts,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByMarts(minMarts, pageable);
        return ResponseEntity.ok(buildings);
    }
    
    // 병원 개수로 검색 (외부 API 연동 예정)
    @GetMapping("/search/filters/hospitals")
    public ResponseEntity<Page<Building>> searchBuildingsByHospitals(
            @RequestParam Integer minHospitals,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Building> buildings = buildingService.searchBuildingsByHospitals(minHospitals, pageable);
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
    
    // ========== 건물 상세 페이지 탭별 API ==========
    
    // 기본 정보 탭
    @GetMapping("/{buildingId}/building-info")
    public ResponseEntity<Building> getBuildingInfo(@PathVariable Long buildingId) {
        Optional<Building> building = buildingService.getBuildingById(buildingId);
        if (building.isPresent()) {
            Building formattedBuilding = buildingService.getFormattedBuilding(building.get());
            return ResponseEntity.ok(formattedBuilding);
        }
        return ResponseEntity.notFound().build();
    }
    
    // 실거주자 후기 탭
    @GetMapping("/{buildingId}/reviews")
    public ResponseEntity<Page<BuildingReview>> getBuildingReviews(
            @PathVariable Long buildingId,
            @RequestParam(value = "sort", defaultValue = "newest") String sortBy,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BuildingReview> reviews = buildingReviewService.getReviewsByBuildingId(buildingId, sortBy, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    // 후기 작성
    @PostMapping("/{buildingId}/reviews")
    public ResponseEntity<BuildingReview> createBuildingReview(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId,
            @RequestBody BuildingReview review) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            
            // 거주지 인증 여부 확인
            postService.checkResidenceVerification(user.getId());
            
            BuildingReview createdReview = buildingReviewService.createReview(buildingId, user.getId(), review);
            return ResponseEntity.ok(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 후기 수정
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<BuildingReview> updateBuildingReview(
            @RequestHeader("Authorization") String token,
            @PathVariable Long reviewId,
            @RequestBody BuildingReview updatedReview) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            
            BuildingReview review = buildingReviewService.updateReview(reviewId, user.getId(), updatedReview);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // 후기 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteBuildingReview(
            @RequestHeader("Authorization") String token,
            @PathVariable Long reviewId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            
            buildingReviewService.deleteReview(reviewId, user.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 질문하기 탭 (Post 기반)
    @GetMapping("/{buildingId}/qnas")
    public ResponseEntity<Page<Post>> getBuildingQuestions(
            @PathVariable Long buildingId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Post> questions = postService.getQuestionsByBuildingId(buildingId, pageable);
        return ResponseEntity.ok(questions);
    }
    
    // 건물별 질문 작성
    @PostMapping("/{buildingId}/questions")
    public ResponseEntity<Post> createBuildingQuestion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId,
            @RequestBody QuestionRequest request) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            
            // 거주지 인증 여부 확인
            postService.checkResidenceVerification(user.getId());
            
            Post question = postService.createBuildingQuestion(user.getId(), buildingId, request.getTitle(), request.getContent());
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 양도 탭 (Post 기반)
    @GetMapping("/{buildingId}/transfers")
    public ResponseEntity<Page<Post>> getBuildingTransfers(
            @PathVariable Long buildingId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<Post> transfers = postService.getTransfersByBuildingId(buildingId, pageable);
        return ResponseEntity.ok(transfers);
    }
    
    // 건물별 양도 글 작성
    @PostMapping("/{buildingId}/transfers")
    public ResponseEntity<Post> createBuildingTransfer(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId,
            @RequestBody QuestionRequest request) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Post transfer = postService.createBuildingTransfer(user.getId(), buildingId, request.getTitle(), request.getContent());
            return ResponseEntity.ok(transfer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // ========== 주변 생활시설 관련 API ==========
    
    /**
     * 특정 건물의 주변 생활시설 개수를 조회합니다.
     * 
     * @param buildingId 건물 ID
     * @return 주변 생활시설 개수
     */
    @GetMapping("/{buildingId}/nearby-facilities")
    public ResponseEntity<Map<String, Integer>> getNearbyFacilityCounts(@PathVariable Long buildingId) {
        try {
            Map<String, Integer> counts = buildingService.getNearbyFacilityCounts(buildingId);
            return ResponseEntity.ok(counts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 건물의 주변 생활시설 개수를 업데이트합니다.
     * 
     * @param buildingId 건물 ID
     * @return 업데이트된 건물 정보
     */
    @PostMapping("/{buildingId}/nearby-facilities/update")
    public ResponseEntity<Building> updateNearbyFacilityCounts(@PathVariable Long buildingId) {
        try {
            Building updatedBuilding = buildingService.updateNearbyFacilityCounts(buildingId);
            return ResponseEntity.ok(updatedBuilding);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 모든 건물의 주변 생활시설 개수를 업데이트합니다.
     * 
     * @return 업데이트된 건물 수
     */
    @PostMapping("/nearby-facilities/update-all")
    public ResponseEntity<Map<String, Object>> updateAllBuildingsNearbyFacilityCounts() {
        try {
            int updatedCount = buildingService.updateAllBuildingsNearbyFacilityCounts();
            return ResponseEntity.ok(Map.of("updatedCount", updatedCount, "message", "업데이트 완료"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 건물들의 주변 생활시설 개수를 업데이트합니다.
     * 
     * @param buildingIds 업데이트할 건물 ID 목록
     * @return 업데이트된 건물 수
     */
    @PostMapping("/nearby-facilities/update-selected")
    public ResponseEntity<Map<String, Object>> updateSelectedBuildingsNearbyFacilityCounts(
            @RequestBody List<Long> buildingIds) {
        try {
            int updatedCount = buildingService.updateBuildingsNearbyFacilityCounts(buildingIds);
            return ResponseEntity.ok(Map.of("updatedCount", updatedCount, "message", "업데이트 완료"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 건물의 후기 키워드 통계를 조회합니다.
     * 좋은 점과 아쉬운 점 각각의 Top3 키워드와 비율을 반환합니다.
     * 
     * @param buildingId 건물 ID
     * @return 후기 키워드 통계 정보
     */
    @GetMapping("/{buildingId}/review-stats")
    public ResponseEntity<BuildingReviewStatsDto> getBuildingReviewStats(@PathVariable Long buildingId) {
        try {
            BuildingReviewStatsDto stats = buildingReviewService.getReviewKeywordStats(buildingId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 건물의 후기 평점 통계를 조회합니다.
     * 만족도 평균과 각 카테고리별 평균 퍼센트를 반환합니다.
     * 
     * @param buildingId 건물 ID
     * @return 후기 평점 통계 정보
     */
    @GetMapping("/{buildingId}/rating-stats")
    public ResponseEntity<BuildingRatingStatsDto> getBuildingRatingStats(@PathVariable Long buildingId) {
        try {
            BuildingRatingStatsDto stats = buildingReviewService.getBuildingRatingStats(buildingId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}
