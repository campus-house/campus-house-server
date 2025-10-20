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
@Table(name = "facilities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Facility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String businessName; // 사업장명
    
    @Column(nullable = false)
    private String address; // 소재지지번주소
    
    @Column
    private String roadAddress; // 도로명주소
    
    @Column(nullable = false)
    private String businessStatus; // 영업상태 (영업중, 휴업, 폐업 등)
    
    @Column
    private String category; // 업종 카테고리 (CONVENIENCE_STORE, MART, HOSPITAL)
    
    @Column
    private String subCategory; // 세부 업종 (편의점, 슈퍼마켓, 대형마트, 종합병원, 의원 등)
    
    @Column
    private Double latitude; // 위도
    
    @Column
    private Double longitude; // 경도
    
    @Column
    private String phoneNumber; // 전화번호
    
    @Column
    private String businessHours; // 영업시간
    
    @Column
    private String description; // 설명
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // 생활시설 카테고리 enum
    public enum Category {
        CONVENIENCE_STORE("편의점"),
        MART("마트"),
        HOSPITAL("병원"),
        PHARMACY("약국"),
        BANK("은행"),
        CAFE("카페"),
        RESTAURANT("음식점"),
        ETC("기타");
        
        private final String description;
        
        Category(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 영업상태 enum
    public enum BusinessStatus {
        OPERATING("영업중"),
        SUSPENDED("휴업"),
        CLOSED("폐업"),
        UNKNOWN("미확인");
        
        private final String description;
        
        BusinessStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
