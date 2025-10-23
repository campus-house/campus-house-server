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
    @Column(name = "user_id")
    private Long userId;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String username; // 로그인용 아이디
    
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
    
    /**
     * 대표 캐릭터 ID에 따른 프로필 이미지를 반환합니다.
     * 대표 캐릭터가 설정되어 있으면 해당 캐릭터의 이미지를,
     * 그렇지 않으면 기본 프로필 이미지를 반환합니다.
     * 
     * @return 프로필 이미지 URL
     */
    public String getEffectiveProfileImage() {
        if (mainCharacterId != null) {
            // 대표 캐릭터가 설정된 경우, 해당 캐릭터의 이미지를 찾아서 반환
            return userCharacters.stream()
                    .filter(uc -> uc.getCharacter().getId().equals(mainCharacterId))
                    .filter(uc -> uc.getIsMain())
                    .map(uc -> uc.getCharacter().getImageUrl())
                    .findFirst()
                    .orElse(profileImage); // 대표 캐릭터를 찾지 못한 경우 기본 프로필 이미지 반환
        }
        return profileImage; // 대표 캐릭터가 설정되지 않은 경우 기본 프로필 이미지 반환
    }
    
    /**
     * 대표 캐릭터 정보를 반환합니다.
     * 
     * @return 대표 캐릭터 정보 (없으면 null)
     */
    public Character getMainCharacter() {
        if (mainCharacterId == null) {
            return null;
        }
        
        return userCharacters.stream()
                .filter(uc -> uc.getCharacter().getId().equals(mainCharacterId))
                .filter(uc -> uc.getIsMain())
                .map(UserCharacter::getCharacter)
                .findFirst()
                .orElse(null);
    }
    
}
