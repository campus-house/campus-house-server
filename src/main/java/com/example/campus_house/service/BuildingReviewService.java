package com.example.campus_house.service;

import com.example.campus_house.entity.Building;
import com.example.campus_house.entity.BuildingReview;
import com.example.campus_house.entity.User;
import com.example.campus_house.entity.ReviewKeyword;
import com.example.campus_house.dto.BuildingReviewStatsDto;
import com.example.campus_house.dto.ReviewKeywordStatsDto;
import com.example.campus_house.dto.BuildingRatingStatsDto;
import com.example.campus_house.repository.BuildingReviewRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingReviewService {
    
    private final BuildingReviewRepository buildingReviewRepository;
    private final BuildingService buildingService;
    private final UserRepository userRepository;
    
    // 특정 건물의 후기 목록 조회 (페이징)
    public Page<BuildingReview> getReviewsByBuildingId(Long buildingId, Pageable pageable) {
        return buildingReviewRepository.findByBuildingId(buildingId, pageable);
    }
    
    // 특정 건물의 후기 목록 조회 (정렬 옵션 포함)
    public Page<BuildingReview> getReviewsByBuildingId(Long buildingId, String sortBy, Pageable pageable) {
        switch (sortBy.toLowerCase()) {
            case "oldest":
                return buildingReviewRepository.findByBuildingIdOrderByCreatedAtAsc(buildingId, pageable);
            case "highest_rating":
                return buildingReviewRepository.findByBuildingIdOrderBySatisfactionDesc(buildingId, pageable);
            case "lowest_rating":
                return buildingReviewRepository.findByBuildingIdOrderBySatisfactionAsc(buildingId, pageable);
            case "newest":
            default:
                return buildingReviewRepository.findByBuildingIdOrderByCreatedAtDesc(buildingId, pageable);
        }
    }
    
    // 특정 건물의 후기 목록 조회 (리스트)
    public List<BuildingReview> getReviewsByBuildingId(Long buildingId) {
        return buildingReviewRepository.findByBuildingId(buildingId);
    }
    
    // 특정 사용자의 후기 목록 조회 (페이징)
    public Page<BuildingReview> getReviewsByUserId(Long userId, Pageable pageable) {
        return buildingReviewRepository.findByUserId(userId, pageable);
    }
    
    // 특정 사용자의 후기 목록 조회 (리스트)
    public List<BuildingReview> getReviewsByUserId(Long userId) {
        return buildingReviewRepository.findByUserId(userId);
    }
    
    // 특정 건물의 후기 수
    public Long getReviewCountByBuildingId(Long buildingId) {
        return buildingReviewRepository.countByBuildingId(buildingId);
    }
    
    // 특정 사용자의 후기 수
    public Long getReviewCountByUserId(Long userId) {
        return buildingReviewRepository.countByUserId(userId);
    }
    
    // 최신순 후기 조회
    public Page<BuildingReview> getReviewsByBuildingIdOrderByCreatedAtDesc(Long buildingId, Pageable pageable) {
        return buildingReviewRepository.findByBuildingIdOrderByCreatedAtDesc(buildingId, pageable);
    }
    
    // 후기 작성
    @Transactional
    public BuildingReview createReview(Long userId, Long buildingId, BuildingReview review) {
        // 건물 존재 확인
        Building building = buildingService.getBuildingById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 후기 생성
        BuildingReview newReview = BuildingReview.builder()
                .building(building)
                .user(user)
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .satisfaction(review.getSatisfaction())
                .residenceType(review.getResidenceType())
                .residencePeriod(review.getResidencePeriod())
                .direction(review.getDirection())
                .noiseRating(review.getNoiseRating())
                .facilityRating(review.getFacilityRating())
                .parkingRating(review.getParkingRating())
                .bugRating(review.getBugRating())
                .keywords(review.getKeywords())
                .build();
        
        return buildingReviewRepository.save(newReview);
    }
    
    // 후기 수정
    @Transactional
    public BuildingReview updateReview(Long reviewId, Long userId, BuildingReview updatedReview) {
        BuildingReview review = buildingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!review.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 후기만 수정할 수 있습니다.");
        }
        
        // 필드 업데이트
        if (updatedReview.getContent() != null) {
            review.setContent(updatedReview.getContent());
        }
        if (updatedReview.getImageUrl() != null) {
            review.setImageUrl(updatedReview.getImageUrl());
        }
        if (updatedReview.getSatisfaction() != null) {
            review.setSatisfaction(updatedReview.getSatisfaction());
        }
        if (updatedReview.getResidenceType() != null) {
            review.setResidenceType(updatedReview.getResidenceType());
        }
        if (updatedReview.getResidencePeriod() != null) {
            review.setResidencePeriod(updatedReview.getResidencePeriod());
        }
        if (updatedReview.getDirection() != null) {
            review.setDirection(updatedReview.getDirection());
        }
        if (updatedReview.getNoiseRating() != null) {
            review.setNoiseRating(updatedReview.getNoiseRating());
        }
        if (updatedReview.getFacilityRating() != null) {
            review.setFacilityRating(updatedReview.getFacilityRating());
        }
        if (updatedReview.getParkingRating() != null) {
            review.setParkingRating(updatedReview.getParkingRating());
        }
        if (updatedReview.getBugRating() != null) {
            review.setBugRating(updatedReview.getBugRating());
        }
        if (updatedReview.getKeywords() != null) {
            review.setKeywords(updatedReview.getKeywords());
        }
        
        return buildingReviewRepository.save(review);
    }
    
    // 후기 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        BuildingReview review = buildingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!review.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 후기만 삭제할 수 있습니다.");
        }
        
        buildingReviewRepository.delete(review);
    }
    
    
    // 건물별 후기 키워드 통계 조회
    public BuildingReviewStatsDto getReviewKeywordStats(Long buildingId) {
        List<BuildingReview> reviews = buildingReviewRepository.findByBuildingId(buildingId);
        
        if (reviews.isEmpty()) {
            return BuildingReviewStatsDto.builder()
                    .goodPoints(new ArrayList<>())
                    .disappointingPoints(new ArrayList<>())
                    .totalReviewCount(0L)
                    .build();
        }
        
        // 좋은 점 키워드들
        Set<ReviewKeyword> goodKeywords = Set.of(
            ReviewKeyword.NEAR_STATION,
            ReviewKeyword.NEAR_SCHOOL,
            ReviewKeyword.CONVENIENT_FACILITIES,
            ReviewKeyword.WIFI_AVAILABLE,
            ReviewKeyword.PARKING_AVAILABLE,
            ReviewKeyword.ELEVATOR_AVAILABLE,
            ReviewKeyword.CLEAN,
            ReviewKeyword.NO_BUGS,
            ReviewKeyword.GOOD_LIGHTING,
            ReviewKeyword.GOOD_VALUE,
            ReviewKeyword.QUIET,
            ReviewKeyword.SECURE
        );
        
        // 아쉬운 점 키워드들
        Set<ReviewKeyword> disappointingKeywords = Set.of(
            ReviewKeyword.FAR_FROM_STATION,
            ReviewKeyword.FAR_FROM_SCHOOL,
            ReviewKeyword.FAR_FROM_FACILITIES,
            ReviewKeyword.NO_WIFI,
            ReviewKeyword.NO_PARKING,
            ReviewKeyword.NO_ELEVATOR,
            ReviewKeyword.OLD,
            ReviewKeyword.MANY_BUGS,
            ReviewKeyword.HUMID,
            ReviewKeyword.EXPENSIVE,
            ReviewKeyword.NOISY,
            ReviewKeyword.INSECURE
        );
        
        // 키워드별 카운트 계산
        Map<ReviewKeyword, Long> keywordCounts = new HashMap<>();
        
        for (BuildingReview review : reviews) {
            if (review.getKeywords() != null) {
                for (ReviewKeyword keyword : review.getKeywords()) {
                    keywordCounts.put(keyword, keywordCounts.getOrDefault(keyword, 0L) + 1);
                }
            }
        }
        
        // 좋은 점 Top3 계산
        List<ReviewKeywordStatsDto> goodPoints = keywordCounts.entrySet().stream()
                .filter(entry -> goodKeywords.contains(entry.getKey()))
                .map(entry -> new ReviewKeywordStatsDto(entry.getKey(), entry.getValue(), (long) reviews.size()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .limit(3)
                .collect(Collectors.toList());
        
        // 아쉬운 점 Top3 계산
        List<ReviewKeywordStatsDto> disappointingPoints = keywordCounts.entrySet().stream()
                .filter(entry -> disappointingKeywords.contains(entry.getKey()))
                .map(entry -> new ReviewKeywordStatsDto(entry.getKey(), entry.getValue(), (long) reviews.size()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .limit(3)
                .collect(Collectors.toList());
        
        return BuildingReviewStatsDto.builder()
                .goodPoints(goodPoints)
                .disappointingPoints(disappointingPoints)
                .totalReviewCount((long) reviews.size())
                .build();
    }
    
    // 건물별 후기 평점 통계 조회
    public BuildingRatingStatsDto getBuildingRatingStats(Long buildingId) {
        List<BuildingReview> reviews = buildingReviewRepository.findByBuildingId(buildingId);
        
        if (reviews.isEmpty()) {
            return BuildingRatingStatsDto.builder()
                    .averageSatisfaction(0.0)
                    .totalReviewCount(0L)
                    .noisePercentage(0.0)
                    .facilityPercentage(0.0)
                    .parkingPercentage(0.0)
                    .bugPercentage(0.0)
                    .build();
        }
        
        // 만족도 평균 계산
        double satisfactionSum = reviews.stream()
                .mapToDouble(review -> review.getSatisfaction() != null ? review.getSatisfaction() : 0)
                .sum();
        double averageSatisfaction = Math.round(satisfactionSum / reviews.size() * 10.0) / 10.0;
        
        // 각 카테고리별 평균 퍼센트 계산
        double noiseSum = reviews.stream()
                .mapToDouble(review -> review.getNoiseRating() != null ? review.getNoiseRating() : 0)
                .sum();
        double noisePercentage = Math.round(noiseSum / reviews.size() / 5.0 * 100 * 10.0) / 10.0;
        
        double facilitySum = reviews.stream()
                .mapToDouble(review -> review.getFacilityRating() != null ? review.getFacilityRating() : 0)
                .sum();
        double facilityPercentage = Math.round(facilitySum / reviews.size() / 5.0 * 100 * 10.0) / 10.0;
        
        double parkingSum = reviews.stream()
                .mapToDouble(review -> review.getParkingRating() != null ? review.getParkingRating() : 0)
                .sum();
        double parkingPercentage = Math.round(parkingSum / reviews.size() / 5.0 * 100 * 10.0) / 10.0;
        
        double bugSum = reviews.stream()
                .mapToDouble(review -> review.getBugRating() != null ? review.getBugRating() : 0)
                .sum();
        double bugPercentage = Math.round(bugSum / reviews.size() / 5.0 * 100 * 10.0) / 10.0;
        
        return BuildingRatingStatsDto.builder()
                .averageSatisfaction(averageSatisfaction)
                .totalReviewCount((long) reviews.size())
                .noisePercentage(noisePercentage)
                .facilityPercentage(facilityPercentage)
                .parkingPercentage(parkingPercentage)
                .bugPercentage(bugPercentage)
                .build();
    }
}
