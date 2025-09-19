package com.example.campus_house.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "residence_verifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ResidenceVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "building_id")
    private Long buildingId; // 건물 ID
    
    @Column(name = "building_name", nullable = false)
    private String buildingName; // 건물명
    
    @Column(name = "building_address", nullable = false)
    private String buildingAddress; // 건물 주소
    
    @Column(name = "room_number")
    private String roomNumber; // 호수 (선택사항)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status; // PENDING, APPROVED, REJECTED
    
    @Column(name = "verification_document")
    private String verificationDocument; // 인증 서류 (임대계약서, 통장 등)
    
    @Column(name = "admin_comment")
    private String adminComment; // 관리자 코멘트
    
    @Column(name = "verified_by")
    private Long verifiedBy; // 인증한 관리자 ID
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt; // 인증 완료 시간
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum VerificationStatus {
        PENDING,    // 대기 중
        APPROVED,   // 승인됨
        REJECTED    // 거부됨
    }
}
