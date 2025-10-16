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
    
    @Column(nullable = false)
    private String title; // 후기 제목
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 후기 내용
    
    @Column
    private String imageUrl; // 후기 이미지
    
    @Column(nullable = false)
    private Integer rating; // 평점 (1-5)
    
    @Column
    private Integer noiseLevel; // 소음 수준 (1-5)
    
    @Column
    private Integer safetyLevel; // 안전 수준 (1-5)
    
    @Column
    private Integer convenienceLevel; // 편의성 수준 (1-5)
    
    @Column
    private Integer managementLevel; // 관리 수준 (1-5)
    
    @Column(columnDefinition = "TEXT")
    private String pros; // 장점
    
    @Column(columnDefinition = "TEXT")
    private String cons; // 단점
    
    @Column
    private String livingPeriod; // 거주 기간
    
    @Column
    private Integer likeCount; // 좋아요 수
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
