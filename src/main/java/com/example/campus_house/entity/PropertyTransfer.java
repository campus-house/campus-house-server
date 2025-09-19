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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_transfers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PropertyTransfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 양도자
    
    @Column(nullable = false)
    private String title; // 양도 제목
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 양도 내용
    
    @Column
    private String imageUrl; // 양도 이미지
    
    @Column
    private BigDecimal transferFee; // 양도비
    
    @Column
    private BigDecimal deposit; // 보증금
    
    @Column
    private BigDecimal monthlyRent; // 월세
    
    @Column
    private LocalDate moveInDate; // 입주 가능일
    
    @Column
    private LocalDate moveOutDate; // 퇴실 예정일
    
    @Column
    private String contactInfo; // 연락처
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferType type; // 양도 타입
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status; // 양도 상태
    
    @Column
    private Integer viewCount; // 조회수
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum TransferType {
        TRANSFER,   // 양도
        RENEWAL     // 재계약
    }
    
    public enum TransferStatus {
        AVAILABLE,  // 양도가능
        COMPLETED,  // 양도완료
        CANCELLED   // 양도취소
    }
}
