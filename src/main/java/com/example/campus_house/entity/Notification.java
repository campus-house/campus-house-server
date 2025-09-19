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
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림을 받을 사용자
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private User fromUser; // 알림을 보낸 사용자 (선택사항)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type; // 알림 타입
    
    @Column(nullable = false)
    private String title; // 알림 제목
    
    @Column(columnDefinition = "TEXT")
    private String content; // 알림 내용
    
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false; // 읽음 여부
    
    @Column(name = "related_id")
    private String relatedId; // 관련 ID (게시글 ID, 댓글 ID 등)
    
    @Column(name = "related_type")
    private String relatedType; // 관련 타입 (POST, COMMENT, MEMO 등)
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        // 게시글 관련
        POST_LIKE,          // 게시글 좋아요
        POST_COMMENT,       // 게시글 댓글
        POST_BOOKMARK,      // 게시글 북마크 (선택사항)
        
        // 댓글 관련
        COMMENT_LIKE,       // 댓글 좋아요
        COMMENT_REPLY,      // 댓글 답글
        
        // 메모 관련
        MEMO_PARTICIPATE,   // 메모 참여
        MEMO_REPLY,         // 메모 답장
        
        // 거주지 인증 관련
        VERIFICATION_APPROVED,  // 거주지 인증 승인
        VERIFICATION_REJECTED,  // 거주지 인증 거부
        
        // 포인트 관련
        POINT_EARNED,       // 포인트 획득
        POINT_USED,         // 포인트 사용
        
        // 캐릭터 관련
        CHARACTER_OBTAINED, // 캐릭터 획득
        CHARACTER_GACHA,    // 가챠 결과
        
        // 시스템 관련
        SYSTEM_NOTICE,      // 시스템 공지
        WELCOME            // 환영 메시지
    }
}
