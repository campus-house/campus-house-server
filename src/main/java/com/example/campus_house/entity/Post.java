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
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory boardCategory; // NEIGHBORHOOD, APARTMENT
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType; // QUESTION, GENERAL
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column
    private String location; // 위치 정보 (질문 게시판용)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status; // ACTIVE, CLOSED, DELETED
    
    @Column
    private Boolean isNew; // 새 질문 표시용
    
    @Column
    private Integer viewCount; // 조회수
    
    @Column
    private Integer likeCount; // 좋아요 수
    
    @Column
    private Integer commentCount; // 댓글 수
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // 게시글 이미지
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PostImage> images = new ArrayList<>();
    
    // 댓글
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    
    // 좋아요
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
    
    // 저장
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();
    
    public enum BoardCategory {
        NEIGHBORHOOD,  // 동네 게시판
        APARTMENT      // 내 아파트 게시판
    }
    
    public enum BoardType {
        QUESTION, GENERAL
    }
    
    public enum PostStatus {
        ACTIVE, CLOSED, DELETED
    }
}
