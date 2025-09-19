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
@Table(name = "properties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String buildingName; // 건물명
    
    @Column(nullable = false)
    private String address; // 주소
    
    @Column
    private String detailAddress; // 상세주소
    
    @Column(nullable = false)
    private Double latitude; // 위도
    
    @Column(nullable = false)
    private Double longitude; // 경도
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType; // 매물 타입 (DEPOSIT/LEASE/MONTHLY)
    
    @Column(nullable = false)
    private BigDecimal deposit; // 보증금
    
    @Column
    private BigDecimal monthlyRent; // 월세 (전세/월세인 경우)
    
    @Column
    private BigDecimal managementFee; // 관리비
    
    @Column
    private Integer floor; // 층수
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FloorType floorType; // 층수 타입 (GROUND/BASEMENT/ROOFTOP)
    
    @Column
    private Double area; // 면적 (평방미터)
    
    @Column
    private Integer rooms; // 방 개수
    
    @Column
    private Integer bathrooms; // 화장실 개수
    
    @Column
    private String structure; // 구조 (원룸, 투룸 등)
    
    @Column(columnDefinition = "TEXT")
    private String description; // 매물 설명
    
    @Column(columnDefinition = "TEXT")
    private String options; // 옵션 (JSON 형태로 저장)
    
    @Column
    private String contactInfo; // 연락처
    
    @Column
    private String agentName; // 중개인명
    
    @Column
    private String agentPhone; // 중개인 전화번호
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status; // 매물 상태 (AVAILABLE/RENTED/MAINTENANCE)
    
    @Column
    private Boolean isScraped; // 스크랩 여부 (사용자별로 관리)
    
    @Column
    private Integer viewCount; // 조회수
    
    @Column
    private Integer scrapCount; // 스크랩 수
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // 매물 이미지
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyImage> images = new ArrayList<>();
    
    // 매물 후기
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyReview> reviews = new ArrayList<>();
    
    // 매물 Q&A
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyQnA> qnas = new ArrayList<>();
    
    // 양도 정보
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyTransfer> transfers = new ArrayList<>();
    
    // 스크랩
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyScrap> scraps = new ArrayList<>();
    
    public enum PropertyType {
        DEPOSIT,    // 전세
        LEASE,      // 임대
        MONTHLY     // 월세
    }
    
    public enum FloorType {
        GROUND,     // 지상
        BASEMENT,   // 반지하
        ROOFTOP     // 옥탑
    }
    
    public enum PropertyStatus {
        AVAILABLE,  // 거래가능
        RENTED,     // 임대완료
        MAINTENANCE // 보수중
    }
}
