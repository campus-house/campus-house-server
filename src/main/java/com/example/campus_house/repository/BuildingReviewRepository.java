package com.example.campus_house.repository;

import com.example.campus_house.entity.BuildingReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingReviewRepository extends JpaRepository<BuildingReview, Long> {
    
    // 특정 건물의 후기 목록 조회 (페이징)
    Page<BuildingReview> findByBuildingId(Long buildingId, Pageable pageable);
    
    // 특정 건물의 후기 목록 조회 (리스트)
    List<BuildingReview> findByBuildingId(Long buildingId);
    
    // 특정 사용자의 후기 목록 조회 (페이징)
    @Query("SELECT br FROM BuildingReview br WHERE br.user.userId = :userId")
    Page<BuildingReview> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 특정 사용자의 후기 목록 조회 (리스트)
    @Query("SELECT br FROM BuildingReview br WHERE br.user.userId = :userId")
    List<BuildingReview> findByUserId(@Param("userId") Long userId);
    
    // 특정 건물의 후기 수
    Long countByBuildingId(Long buildingId);
    
    // 특정 사용자의 후기 수
    @Query("SELECT COUNT(br) FROM BuildingReview br WHERE br.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    // 최신순 후기 조회
    Page<BuildingReview> findByBuildingIdOrderByCreatedAtDesc(Long buildingId, Pageable pageable);
    
    // 오래된 순 후기 조회
    Page<BuildingReview> findByBuildingIdOrderByCreatedAtAsc(Long buildingId, Pageable pageable);
    
    // 높은 별점순 후기 조회 (만족도 기준)
    Page<BuildingReview> findByBuildingIdOrderBySatisfactionDesc(Long buildingId, Pageable pageable);
    
    // 낮은 별점순 후기 조회 (만족도 기준)
    Page<BuildingReview> findByBuildingIdOrderBySatisfactionAsc(Long buildingId, Pageable pageable);
}
