package com.example.campus_house.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Building {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String buildingName; // 건물명
    
    @Column(nullable = false)
    private String address; // 건물 전체 주소 (예: "서울시 관악구 신림동 123-45")
    
    @Column(nullable = false)
    private Double latitude; // 위도
    
    @Column(nullable = false)
    private Double longitude; // 경도
    
    
    @Column(nullable = false)
    private BigDecimal deposit; // 보증금
    
    @Column
    private BigDecimal monthlyRent; // 월세 (전세/월세인 경우)
    
    @Column
    private BigDecimal jeonse; // 전세 (만원)
    
    @Column
    private Integer households; // 세대수
    
    @Column
    private String heatingType; // 난방방식
    
    @Column
    private Integer parkingSpaces; // 주차대수
    
    @Column
    private Integer elevators; // 승강기대수
    
    @Column(columnDefinition = "TEXT")
    private String buildingUsage; // 건축물용도
    
    @Column
    private LocalDateTime approvalDate; // 사용승인일
    
    @Column
    private LocalDateTime completionDate; // 준공일
    
    @Column
    private Integer nearbyConvenienceStores; // 반경 1km 이내 편의점 개수 (외부 API 연동 예정)
    
    @Column
    private Integer nearbyMarts; // 반경 1km 이내 마트 개수 (외부 API 연동 예정)
    
    @Column
    private Integer nearbyHospitals; // 반경 1km 이내 병원 개수 (외부 API 연동 예정)
    
    @Column
    private Integer schoolWalkingTime; // 학교까지 걸리는 시간 (분)
    
    
    @Column
    private Integer stationWalkingTime; // 영통역까지 걸리는 시간 (분)
    
    @Column
    private Integer scrapCount; // 스크랩 수
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    
    // 건물 후기
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BuildingReview> reviews = new ArrayList<>();
    
    // 건물 관련 질문 (Post 엔티티의 QUESTION 게시판)
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Post> questions = new ArrayList<>();
    
    // 양도 정보 (Post 엔티티의 TRANSFER 게시판)
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Post> transfers = new ArrayList<>();
    
    // 스크랩
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BuildingScrap> scraps = new ArrayList<>();
    
    // 학교 접근성 enum
    public enum SchoolAccessibility {
        WALKING_10MIN,  // 도보 10분 이내
        WALKING_20MIN,  // 도보 10~20분
        WALKING_30MIN   // 도보 30분
    }
    
    // 영통역 접근성 enum
    public enum StationAccessibility {
        WALKING_5MIN,   // 도보 5분 이내
        WALKING_10MIN,  // 도보 10분 이내
        WALKING_15MIN,  // 도보 15분 이내
        WALKING_20MIN   // 도보 20분 이내
    }
}
