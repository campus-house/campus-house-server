package com.example.campus_house.repository;

import com.example.campus_house.entity.BuildingReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingReviewRepository extends JpaRepository<BuildingReview, Long> {
    
    // 특정 건물의 후기 목록 조회 (페이징)
    Page<BuildingReview> findByBuildingId(Long buildingId, Pageable pageable);
    
    // 특정 건물의 후기 목록 조회 (리스트)
    List<BuildingReview> findByBuildingId(Long buildingId);
    
    // 특정 사용자의 후기 목록 조회 (페이징)
    Page<BuildingReview> findByUserId(Long userId, Pageable pageable);
    
    // 특정 사용자의 후기 목록 조회 (리스트)
    List<BuildingReview> findByUserId(Long userId);
    
    // 특정 건물의 후기 수
    Long countByBuildingId(Long buildingId);
    
    // 특정 사용자의 후기 수
    Long countByUserId(Long userId);
    
    // 평점별 후기 조회
    Page<BuildingReview> findByBuildingIdAndRating(Long buildingId, Integer rating, Pageable pageable);
    
    // 최신순 후기 조회
    Page<BuildingReview> findByBuildingIdOrderByCreatedAtDesc(Long buildingId, Pageable pageable);
    
    // 인기순 후기 조회 (좋아요 수 기준)
    Page<BuildingReview> findByBuildingIdOrderByLikeCountDesc(Long buildingId, Pageable pageable);
}
