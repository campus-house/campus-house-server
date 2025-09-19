package com.example.campus_house.service;

import com.example.campus_house.entity.Property;
import com.example.campus_house.entity.PropertyReview;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.PropertyRepository;
import com.example.campus_house.repository.PropertyReviewRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyReviewService {
    
    private final PropertyReviewRepository propertyReviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    
    // 특정 매물의 후기 조회
    @Cacheable(value = "reviews", key = "#propertyId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PropertyReview> getReviewsByPropertyId(Long propertyId, Pageable pageable) {
        return propertyReviewRepository.findByPropertyIdAndStatusOrderByCreatedAtDesc(
                propertyId, PropertyReview.ReviewStatus.ACTIVE, pageable);
    }
    
    // 특정 매물의 후기 수 조회
    public long getReviewCountByPropertyId(Long propertyId) {
        return propertyReviewRepository.countByPropertyIdAndStatus(propertyId, PropertyReview.ReviewStatus.ACTIVE);
    }
    
    // 특정 매물의 평균 평점 조회
    @Cacheable(value = "reviews", key = "'avg_rating_' + #propertyId")
    public Double getAverageRatingByPropertyId(Long propertyId) {
        return propertyReviewRepository.findAverageRatingByPropertyId(propertyId);
    }
    
    // 특정 매물의 평점별 후기 수 조회
    public List<Object[]> getRatingCountByPropertyId(Long propertyId) {
        return propertyReviewRepository.findRatingCountByPropertyId(propertyId);
    }
    
    // 특정 사용자의 후기 조회
    public Page<PropertyReview> getReviewsByUserId(Long userId, Pageable pageable) {
        return propertyReviewRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                userId, PropertyReview.ReviewStatus.ACTIVE, pageable);
    }
    
    // 인기 후기 조회
    public Page<PropertyReview> getPopularReviews(Pageable pageable) {
        return propertyReviewRepository.findPopularReviews(pageable);
    }
    
    // 최근 후기 조회
    public Page<PropertyReview> getRecentReviews(Pageable pageable) {
        return propertyReviewRepository.findByStatusOrderByCreatedAtDesc(PropertyReview.ReviewStatus.ACTIVE, pageable);
    }
    
    // 평점 범위별 후기 조회
    public Page<PropertyReview> getReviewsByRatingRange(Long propertyId, Integer minRating, Integer maxRating, Pageable pageable) {
        return propertyReviewRepository.findByPropertyIdAndRatingRange(propertyId, minRating, maxRating, pageable);
    }
    
    // 후기 작성
    @Transactional
    public PropertyReview createReview(Long propertyId, Long userId, String title, String content, String imageUrl,
                                     Integer rating, Integer noiseLevel, Integer safetyLevel, Integer convenienceLevel,
                                     Integer managementLevel, String pros, String cons, String livingPeriod) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        PropertyReview review = PropertyReview.builder()
                .property(property)
                .user(user)
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .rating(rating)
                .noiseLevel(noiseLevel)
                .safetyLevel(safetyLevel)
                .convenienceLevel(convenienceLevel)
                .managementLevel(managementLevel)
                .pros(pros)
                .cons(cons)
                .livingPeriod(livingPeriod)
                .status(PropertyReview.ReviewStatus.ACTIVE)
                .likeCount(0)
                .build();
        
        return propertyReviewRepository.save(review);
    }
    
    // 후기 수정
    @Transactional
    public PropertyReview updateReview(Long reviewId, Long userId, String title, String content, String imageUrl,
                                     Integer rating, Integer noiseLevel, Integer safetyLevel, Integer convenienceLevel,
                                     Integer managementLevel, String pros, String cons, String livingPeriod) {
        PropertyReview review = propertyReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        // 작성자만 수정 가능
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("후기를 수정할 권한이 없습니다.");
        }
        
        review.setTitle(title);
        review.setContent(content);
        review.setImageUrl(imageUrl);
        review.setRating(rating);
        review.setNoiseLevel(noiseLevel);
        review.setSafetyLevel(safetyLevel);
        review.setConvenienceLevel(convenienceLevel);
        review.setManagementLevel(managementLevel);
        review.setPros(pros);
        review.setCons(cons);
        review.setLivingPeriod(livingPeriod);
        review.setUpdatedAt(LocalDateTime.now());
        
        return propertyReviewRepository.save(review);
    }
    
    // 후기 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        PropertyReview review = propertyReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        // 작성자만 삭제 가능
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("후기를 삭제할 권한이 없습니다.");
        }
        
        review.setStatus(PropertyReview.ReviewStatus.DELETED);
        propertyReviewRepository.save(review);
    }
    
    // 후기 좋아요 수 업데이트
    @Transactional
    public void updateReviewLikeCount(Long reviewId) {
        PropertyReview review = propertyReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        
        review.setLikeCount(review.getLikes().size());
        propertyReviewRepository.save(review);
    }
}
