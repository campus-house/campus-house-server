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

@Entity
@Table(name = "building_transfers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BuildingTransfer {
    
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
    private String title; // 양도 제목
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 양도 내용
    
    @Column
    private String imageUrl; // 양도 이미지
    
    @Column
    private BigDecimal transferFee; // 양도비
    
    @Column
    private String contactInfo; // 연락처
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status; // 양도 상태
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum TransferStatus {
        AVAILABLE,  // 양도 가능
        COMPLETED,  // 양도 완료
        CANCELLED   // 양도 취소
    }
}
