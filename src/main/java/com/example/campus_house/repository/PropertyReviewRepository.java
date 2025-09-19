package com.example.campus_house.repository;

import com.example.campus_house.entity.PropertyReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyReviewRepository extends JpaRepository<PropertyReview, Long> {
    
    // 특정 매물의 후기 조회
    Page<PropertyReview> findByPropertyIdAndStatusOrderByCreatedAtDesc(
            Long propertyId, PropertyReview.ReviewStatus status, Pageable pageable);
    
    // 특정 매물의 후기 수 조회
    long countByPropertyIdAndStatus(Long propertyId, PropertyReview.ReviewStatus status);
    
    // 특정 매물의 평균 평점 조회
    @Query("SELECT AVG(r.rating) FROM PropertyReview r WHERE r.property.id = :propertyId AND r.status = 'ACTIVE'")
    Double findAverageRatingByPropertyId(@Param("propertyId") Long propertyId);
    
    // 특정 매물의 평점별 후기 수 조회
    @Query("SELECT r.rating, COUNT(r) FROM PropertyReview r WHERE r.property.id = :propertyId AND r.status = 'ACTIVE' GROUP BY r.rating")
    List<Object[]> findRatingCountByPropertyId(@Param("propertyId") Long propertyId);
    
    // 특정 사용자의 후기 조회
    Page<PropertyReview> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, PropertyReview.ReviewStatus status, Pageable pageable);
    
    // 인기 후기 (좋아요 수 기준)
    @Query("SELECT r FROM PropertyReview r WHERE r.status = 'ACTIVE' ORDER BY r.likeCount DESC, r.createdAt DESC")
    Page<PropertyReview> findPopularReviews(Pageable pageable);
    
    // 최근 후기
    Page<PropertyReview> findByStatusOrderByCreatedAtDesc(PropertyReview.ReviewStatus status, Pageable pageable);
    
    // 평점 범위별 후기 조회
    @Query("SELECT r FROM PropertyReview r WHERE r.property.id = :propertyId AND r.rating BETWEEN :minRating AND :maxRating AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    Page<PropertyReview> findByPropertyIdAndRatingRange(@Param("propertyId") Long propertyId,
                                                       @Param("minRating") Integer minRating,
                                                       @Param("maxRating") Integer maxRating,
                                                       Pageable pageable);
}
