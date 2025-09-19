package com.example.campus_house.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "memos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Memo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 500)
    private String content;
    
    @Column
    private String imageUrl; // 이미지 첨부 가능
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemoType type; // SHARING(공유), PURCHASE(공동구매), FOOD(음식나눔), ETC(기타)
    
    @Column
    private String location; // 위치 정보
    
    @Column
    private Integer maxParticipants; // 최대 참여자 수 (공동구매용)
    
    @Column
    private Integer currentParticipants; // 현재 참여자 수
    
    @Column
    private String contactInfo; // 연락처 정보
    
    @Column
    private LocalDateTime deadline; // 마감 시간
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemoStatus status; // ACTIVE, EXPIRED, COMPLETED
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt; // 24시간 후 만료
    
    // 메모 답장/채팅
    @OneToMany(mappedBy = "memo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemoReply> replies = new ArrayList<>();
    
    // 메모 참여자
    @OneToMany(mappedBy = "memo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemoParticipant> participants = new ArrayList<>();
    
    public enum MemoType {
        SHARING, PURCHASE, FOOD, ETC
    }
    
    public enum MemoStatus {
        ACTIVE, EXPIRED, COMPLETED
    }
    
    // 메모 생성 시 24시간 후 만료 시간 자동 설정
    @PrePersist
    public void setExpiresAt() {
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }
}
