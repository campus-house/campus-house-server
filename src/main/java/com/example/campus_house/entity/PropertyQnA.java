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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "property_qnas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PropertyQnA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PropertyQnA parent; // 대댓글용
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 질문/답변 내용
    
    @Column
    private String imageUrl; // 이미지 첨부
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QnAType type; // 질문/답변 타입
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QnAStatus status; // 상태
    
    @Column
    private Integer likeCount; // 좋아요 수
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // 대댓글
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyQnA> replies = new ArrayList<>();
    
    // Q&A 좋아요
    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PropertyQnALike> likes = new ArrayList<>();
    
    public enum QnAType {
        QUESTION, ANSWER
    }
    
    public enum QnAStatus {
        ACTIVE, DELETED
    }
}
