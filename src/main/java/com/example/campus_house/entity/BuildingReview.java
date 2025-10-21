package com.example.campus_house.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "building_reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BuildingReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 후기 내용
    
    @Column
    private String imageUrl; // 후기 이미지
    
    // 만족도 (1-5)
    @Column(nullable = false)
    private Integer satisfaction; // 만족도 (1-5)
    
    // 거주 유형
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResidenceType residenceType; // 거주 유형 {아파트, 오피스텔, 주택/빌라, 원투룸}
    
    // 거주 기간
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResidencePeriod residencePeriod; // 거주 기간 {2023년 이전, 2024년까지, 2025년까지}
    
    // 방향
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction; // 방향 {남향, 남동/남서향, 동향, 서향, 북향, 남북/동서향}
    
    // 세부 평점들 (1-5)
    @Column(nullable = false)
    private Integer noiseRating; // 소음 (1-5)
    
    @Column(nullable = false)
    private Integer facilityRating; // 편의시설 (1-5)
    
    @Column(nullable = false)
    private Integer parkingRating; // 주차장 (1-5)
    
    @Column(nullable = false)
    private Integer bugRating; // 벌레 (1-5)
    
    // 키워드 (중복 선택 가능)
    @ElementCollection(targetClass = ReviewKeyword.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "review_keywords", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "keyword")
    private List<ReviewKeyword> keywords; // 키워드 리스트
    
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
