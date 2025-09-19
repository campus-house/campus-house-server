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
@Table(name = "point_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type; // 포인트 타입
    
    @Column(nullable = false)
    private Integer amount; // 포인트 양 (양수: 획득, 음수: 사용)
    
    @Column(nullable = false)
    private Integer balance; // 잔액
    
    @Column
    private String description; // 설명
    
    @Column
    private String relatedId; // 관련 ID (게시글 ID, 캐릭터 ID 등)
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum PointType {
        // 획득
        POST_CREATE,        // 게시글 작성
        COMMENT_CREATE,     // 댓글 작성
        MEMO_CREATE,        // 메모 작성
        POST_LIKE_RECEIVE,  // 게시글 좋아요 받음
        COMMENT_LIKE_RECEIVE, // 댓글 좋아요 받음
        QUESTION_ANSWER,    // 질문 답변
        DAILY_LOGIN,        // 일일 로그인
        WEEKLY_ACTIVE,      // 주간 활동
        
        // 사용
        CHARACTER_GACHA,    // 캐릭터 가챠
        CHARACTER_PURCHASE  // 캐릭터 구매
    }
}
