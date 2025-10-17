package com.example.campus_house.service;

import com.example.campus_house.entity.Building;
import com.example.campus_house.entity.BuildingReview;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.BuildingReviewRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    
    // 평점별 후기 조회
    public Page<BuildingReview> getReviewsByBuildingIdAndRating(Long buildingId, Integer rating, Pageable pageable) {
        return buildingReviewRepository.findByBuildingIdAndRating(buildingId, rating, pageable);
    }
    
    // 최신순 후기 조회
    public Page<BuildingReview> getReviewsByBuildingIdOrderByCreatedAtDesc(Long buildingId, Pageable pageable) {
        return buildingReviewRepository.findByBuildingIdOrderByCreatedAtDesc(buildingId, pageable);
    }
    
    // 인기순 후기 조회 (좋아요 수 기준)
    public Page<BuildingReview> getReviewsByBuildingIdOrderByLikeCountDesc(Long buildingId, Pageable pageable) {
        return buildingReviewRepository.findByBuildingIdOrderByLikeCountDesc(buildingId, pageable);
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
                .title(review.getTitle())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .rating(review.getRating())
                .noiseLevel(review.getNoiseLevel())
                .safetyLevel(review.getSafetyLevel())
                .convenienceLevel(review.getConvenienceLevel())
                .managementLevel(review.getManagementLevel())
                .pros(review.getPros())
                .cons(review.getCons())
                .livingPeriod(review.getLivingPeriod())
                .likeCount(0)
                .build();
        
        return buildingReviewRepository.save(newReview);
    }
    
    // 후기 수정
    @Transactional
    public BuildingReview updateReview(Long reviewId, Long userId, BuildingReview updatedReview) {
        BuildingReview review = buildingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 후기만 수정할 수 있습니다.");
        }
        
        // 필드 업데이트
        if (updatedReview.getTitle() != null) {
            review.setTitle(updatedReview.getTitle());
        }
        if (updatedReview.getContent() != null) {
            review.setContent(updatedReview.getContent());
        }
        if (updatedReview.getImageUrl() != null) {
            review.setImageUrl(updatedReview.getImageUrl());
        }
        if (updatedReview.getRating() != null) {
            review.setRating(updatedReview.getRating());
        }
        if (updatedReview.getNoiseLevel() != null) {
            review.setNoiseLevel(updatedReview.getNoiseLevel());
        }
        if (updatedReview.getSafetyLevel() != null) {
            review.setSafetyLevel(updatedReview.getSafetyLevel());
        }
        if (updatedReview.getConvenienceLevel() != null) {
            review.setConvenienceLevel(updatedReview.getConvenienceLevel());
        }
        if (updatedReview.getManagementLevel() != null) {
            review.setManagementLevel(updatedReview.getManagementLevel());
        }
        if (updatedReview.getPros() != null) {
            review.setPros(updatedReview.getPros());
        }
        if (updatedReview.getCons() != null) {
            review.setCons(updatedReview.getCons());
        }
        if (updatedReview.getLivingPeriod() != null) {
            review.setLivingPeriod(updatedReview.getLivingPeriod());
        }
        
        return buildingReviewRepository.save(review);
    }
    
    // 후기 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        BuildingReview review = buildingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        // 작성자 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 후기만 삭제할 수 있습니다.");
        }
        
        buildingReviewRepository.delete(review);
    }
    
    // 후기 좋아요
    @Transactional
    public void likeReview(Long reviewId) {
        BuildingReview review = buildingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        review.setLikeCount(review.getLikeCount() != null ? review.getLikeCount() + 1 : 1);
        buildingReviewRepository.save(review);
    }
}
