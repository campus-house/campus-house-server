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
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType; // 매물 타입
    
    @Column
    private BigDecimal deposit; // 보증금
    
    @Column
    private BigDecimal monthlyRent; // 월세
    
    @Column
    private BigDecimal managementFee; // 관리비
    
    @Column
    private Integer floor; // 층수
    
    @Enumerated(EnumType.STRING)
    @Column
    private FloorType floorType; // 층수 타입
    
    @Column
    private Double area; // 면적 (제곱미터)
    
    @Column
    private Integer rooms; // 방 개수
    
    @Column
    private Integer bathrooms; // 화장실 개수
    
    @Column
    private String structure; // 구조 (예: 원룸, 투룸)
    
    @Column(columnDefinition = "TEXT")
    private String description; // 매물 설명
    
    @Column(columnDefinition = "TEXT")
    private String options; // 옵션 (예: 에어컨, 냉장고)
    
    @Column
    private String contactInfo; // 연락처
    
    @Column
    private String agentName; // 중개인명
    
    @Column
    private String agentPhone; // 중개인 전화번호
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status; // 매물 상태
    
    @Column
    private Integer viewCount; // 조회수
    
    @Column
    private Integer scrapCount; // 스크랩 수
    
    @Column
    private Double latitude; // 위도
    
    @Column
    private Double longitude; // 경도
    
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
    
    // 매물 양도
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyTransfer> transfers = new ArrayList<>();
    
    // 매물 스크랩
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyScrap> scraps = new ArrayList<>();
    
    // 매물 타입 열거형
    public enum PropertyType {
        APARTMENT,      // 아파트
        VILLA,          // 빌라
        OFFICETEL,      // 오피스텔
        STUDIO,         // 원룸
        SHARED,         // 셰어하우스
        ETC             // 기타
    }
    
    // 층수 타입 열거형
    public enum FloorType {
        GROUND,         // 지하
        FIRST,          // 1층
        SECOND,         // 2층
        THIRD,          // 3층
        FOURTH,         // 4층
        FIFTH,          // 5층
        SIXTH,          // 6층
        SEVENTH,        // 7층
        EIGHTH,         // 8층
        NINTH,          // 9층
        TENTH,          // 10층
        ELEVENTH,       // 11층
        TWELFTH,        // 12층
        THIRTEENTH,     // 13층
        FOURTEENTH,     // 14층
        FIFTEENTH,      // 15층
        SIXTEENTH,      // 16층
        SEVENTEENTH,    // 17층
        EIGHTEENTH,     // 18층
        NINETEENTH,     // 19층
        TWENTIETH,      // 20층
        TWENTY_FIRST,   // 21층
        TWENTY_SECOND,  // 22층
        TWENTY_THIRD,   // 23층
        TWENTY_FOURTH,  // 24층
        TWENTY_FIFTH,   // 25층
        TWENTY_SIXTH,   // 26층
        TWENTY_SEVENTH, // 27층
        TWENTY_EIGHTH,  // 28층
        TWENTY_NINTH,   // 29층
        THIRTIETH,      // 30층
        THIRTY_FIRST,   // 31층
        THIRTY_SECOND,  // 32층
        THIRTY_THIRD,   // 33층
        THIRTY_FOURTH,  // 34층
        THIRTY_FIFTH,   // 35층
        THIRTY_SIXTH,   // 36층
        THIRTY_SEVENTH, // 37층
        THIRTY_EIGHTH,  // 38층
        THIRTY_NINTH,   // 39층
        FORTIETH,       // 40층
        FORTY_FIRST,    // 41층
        FORTY_SECOND,   // 42층
        FORTY_THIRD,    // 43층
        FORTY_FOURTH,   // 44층
        FORTY_FIFTH,    // 45층
        FORTY_SIXTH,    // 46층
        FORTY_SEVENTH,  // 47층
        FORTY_EIGHTH,   // 48층
        FORTY_NINTH,    // 49층
        FIFTIETH,       // 50층
        FIFTY_FIRST,    // 51층
        FIFTY_SECOND,   // 52층
        FIFTY_THIRD,    // 53층
        FIFTY_FOURTH,   // 54층
        FIFTY_FIFTH,    // 55층
        FIFTY_SIXTH,    // 56층
        FIFTY_SEVENTH,  // 57층
        FIFTY_EIGHTH,   // 58층
        FIFTY_NINTH,    // 59층
        SIXTIETH,       // 60층
        SIXTY_FIRST,    // 61층
        SIXTY_SECOND,   // 62층
        SIXTY_THIRD,    // 63층
        SIXTY_FOURTH,   // 64층
        SIXTY_FIFTH,    // 65층
        SIXTY_SIXTH,    // 66층
        SIXTY_SEVENTH,  // 67층
        SIXTY_EIGHTH,   // 68층
        SIXTY_NINTH,    // 69층
        SEVENTIETH,     // 70층
        SEVENTY_FIRST,  // 71층
        SEVENTY_SECOND, // 72층
        SEVENTY_THIRD,  // 73층
        SEVENTY_FOURTH, // 74층
        SEVENTY_FIFTH,  // 75층
        SEVENTY_SIXTH,  // 76층
        SEVENTY_SEVENTH, // 77층
        SEVENTY_EIGHTH, // 78층
        SEVENTY_NINTH,  // 79층
        EIGHTIETH,      // 80층
        EIGHTY_FIRST,   // 81층
        EIGHTY_SECOND,  // 82층
        EIGHTY_THIRD,   // 83층
        EIGHTY_FOURTH,  // 84층
        EIGHTY_FIFTH,   // 85층
        EIGHTY_SIXTH,   // 86층
        EIGHTY_SEVENTH, // 87층
        EIGHTY_EIGHTH,  // 88층
        EIGHTY_NINTH,   // 89층
        NINETIETH,      // 90층
        NINETY_FIRST,   // 91층
        NINETY_SECOND,  // 92층
        NINETY_THIRD,   // 93층
        NINETY_FOURTH,  // 94층
        NINETY_FIFTH,   // 95층
        NINETY_SIXTH,   // 96층
        NINETY_SEVENTH, // 97층
        NINETY_EIGHTH,  // 98층
        NINETY_NINTH,   // 99층
        HUNDREDTH,      // 100층
        OVER_100        // 100층 이상
    }
    
    // 매물 상태 열거형
    public enum PropertyStatus {
        AVAILABLE,      // 매물 가능
        RENTED,         // 임대 완료
        SOLD,           // 매매 완료
        UNAVAILABLE,    // 매물 불가
        DELETED         // 삭제됨
    }
}
