package com.example.campus_house.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String nickname;
    
    @Column
    private String profileImage;
    
    @Column
    private String characterImage; // 캐릭터 이미지 (메모용)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType; // RESIDENT(거주자), NON_RESIDENT(비거주자)
    
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false; // 거주지 인증 여부
    
    @Column(name = "verified_building_id")
    private Long verifiedBuildingId; // 인증된 건물 ID
    
    @Column(name = "verified_building_name")
    private String verifiedBuildingName; // 인증된 건물명
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt; // 인증 완료 시간
    
    @Column(name = "rewards")
    @Builder.Default
    private Integer rewards = 0; // 보유 리워드
    
    @Column(name = "main_character_id")
    private Long mainCharacterId; // 대표 캐릭터 ID
    
    @Column
    private String location; // 거주지역
    
    @Column
    private String university; // 대학교
    
    @Column
    private String major; // 전공
    
    @Column
    private String introduction; // 자기소개
    
    
    // 메모 관련
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Memo> memos = new ArrayList<>();
    
    // 게시글 관련
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();
    
    // 댓글 관련
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    
    // 좋아요 관련
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
    
    // 저장 관련
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();
    
    // 캐릭터 관련
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserCharacter> userCharacters = new ArrayList<>();
    
    // 리워드 내역
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RewardHistory> rewardHistories = new ArrayList<>();
    
    public enum UserType {
        RESIDENT, NON_RESIDENT
    }
    
}
