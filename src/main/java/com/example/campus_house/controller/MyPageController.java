package com.example.campus_house.controller;

import com.example.campus_house.entity.*;
import com.example.campus_house.repository.UserRepository;
import com.example.campus_house.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {
    
    private final UserRepository userRepository;
    private final PostService postService;
    private final BookmarkService bookmarkService;
    private final CharacterService characterService;
    private final PointService pointService;
    private final AuthService authService;
    private final PropertyReviewService propertyReviewService;
    private final com.example.campus_house.repository.ResidenceVerificationRepository residenceVerificationRepository;
    private final CommentService commentService;
    
    // 사용자 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            UserProfile profile = UserProfile.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .characterImage(user.getCharacterImage())
                    .userType(user.getUserType())
                    .isVerified(user.getIsVerified())
                    .verifiedBuildingName(user.getVerifiedBuildingName())
                    .points(user.getPoints())
                    .mainCharacterId(user.getMainCharacterId())
                    .location(user.getLocation())
                    .university(user.getUniversity())
                    .major(user.getMajor())
                    .introduction(user.getIntroduction())
                    .build();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 사용자 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestHeader("Authorization") String token, 
                                            @RequestBody UpdateProfileRequest request) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getProfileImage() != null) {
                user.setProfileImage(request.getProfileImage());
            }
            if (request.getCharacterImage() != null) {
                user.setCharacterImage(request.getCharacterImage());
            }
            if (request.getLocation() != null) {
                user.setLocation(request.getLocation());
            }
            if (request.getUniversity() != null) {
                user.setUniversity(request.getUniversity());
            }
            if (request.getMajor() != null) {
                user.setMajor(request.getMajor());
            }
            if (request.getIntroduction() != null) {
                user.setIntroduction(request.getIntroduction());
            }
            
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 내가 작성한 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getMyPosts(@RequestHeader("Authorization") String token,
                                               @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Page<Post> posts = postService.getPostsByAuthor(user.getId(), pageable);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 내가 작성한 댓글 조회
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getMyComments(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            List<Comment> comments = commentService.getCommentsByAuthor(user.getId());
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 내가 저장한 게시글 조회
    @GetMapping("/bookmarks")
    public ResponseEntity<List<Bookmark>> getMyBookmarks(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            List<Bookmark> bookmarks = bookmarkService.getAllBookmarkedPostsByUserId(user.getId());
            return ResponseEntity.ok(bookmarks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // (이전) 내가 저장한 매물 조회 엔드포인트는 ScrapController로 분리됨
    
    // 보유 캐릭터 조회
    @GetMapping("/characters")
    public ResponseEntity<List<UserCharacter>> getMyCharacters(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            List<UserCharacter> characters = characterService.getUserCharacters(user.getId());
            return ResponseEntity.ok(characters);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 대표 캐릭터 설정
    @PostMapping("/characters/{characterId}/set-main")
    public ResponseEntity<Void> setMainCharacter(@RequestHeader("Authorization") String token,
                                               @PathVariable Long characterId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            characterService.setMainCharacter(user.getId(), characterId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 캐릭터 가챠
    @PostMapping("/characters/gacha")
    public ResponseEntity<com.example.campus_house.entity.Character> performGacha(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            com.example.campus_house.entity.Character result = characterService.performGacha(user.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 캐릭터 구매
    @PostMapping("/characters/{characterId}/purchase")
    public ResponseEntity<com.example.campus_house.entity.Character> purchaseCharacter(@RequestHeader("Authorization") String token,
                                                                                     @PathVariable Long characterId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            com.example.campus_house.entity.Character result = characterService.purchaseCharacter(user.getId(), characterId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 포인트 내역 조회
    @GetMapping("/points/history")
    public ResponseEntity<Page<PointHistory>> getPointHistory(@RequestHeader("Authorization") String token,
                                                            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Page<PointHistory> history = pointService.getPointHistory(user.getId(), pageable);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 포인트 통계 조회
    @GetMapping("/points/stats")
    public ResponseEntity<PointService.UserPointStats> getPointStats(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            PointService.UserPointStats stats = pointService.getUserPointStats(user.getId());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 내 거주지 조회 (승인된 인증 기준)
    @GetMapping("/residence")
    public ResponseEntity<ResidenceInfo> getMyResidence(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            java.util.Optional<ResidenceVerification> approved =
                    residenceVerificationRepository.findByUserIdAndStatus(
                            user.getId(), ResidenceVerification.VerificationStatus.APPROVED);
            if (approved.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            ResidenceVerification v = approved.get();
            ResidenceInfo info = ResidenceInfo.builder()
                    .buildingId(v.getBuildingId())
                    .buildingName(v.getBuildingName())
                    .buildingAddress(v.getBuildingAddress())
                    .roomNumber(v.getRoomNumber())
                    .verifiedAt(v.getVerifiedAt())
                    .build();
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 내가 남긴 매물 후기 조회
    @GetMapping("/property-reviews")
    public ResponseEntity<Page<PropertyReview>> getMyPropertyReviews(
            @RequestHeader("Authorization") String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Page<PropertyReview> reviews = propertyReviewService.getReviewsByUserId(user.getId(), pageable);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 매물 후기 작성
    @PostMapping("/property-reviews")
    public ResponseEntity<PropertyReview> createMyPropertyReview(
            @RequestHeader("Authorization") String token,
            @RequestBody CreatePropertyReviewRequest request) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            PropertyReview review = propertyReviewService.createReview(
                    request.getPropertyId(),
                    user.getId(),
                    request.getTitle(),
                    request.getContent(),
                    request.getImageUrl(),
                    request.getRating(),
                    request.getNoiseLevel(),
                    request.getSafetyLevel(),
                    request.getConvenienceLevel(),
                    request.getManagementLevel(),
                    request.getPros(),
                    request.getCons(),
                    request.getLivingPeriod()
            );
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 캐릭터 통계 조회
    @GetMapping("/characters/stats")
    public ResponseEntity<CharacterService.UserCharacterStats> getCharacterStats(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            CharacterService.UserCharacterStats stats = characterService.getUserCharacterStats(user.getId());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTO 클래스들
    @lombok.Data
    @lombok.Builder
    public static class UserProfile {
        private Long id;
        private String email;
        private String nickname;
        private String profileImage;
        private String characterImage;
        private User.UserType userType;
        private Boolean isVerified;
        private String verifiedBuildingName;
        private Integer points;
        private Long mainCharacterId;
        private String location;
        private String university;
        private String major;
        private String introduction;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class ResidenceInfo {
        private Long buildingId;
        private String buildingName;
        private String buildingAddress;
        private String roomNumber;
        private java.time.LocalDateTime verifiedAt;
    }

    @lombok.Data
    public static class CreatePropertyReviewRequest {
        private Long propertyId;
        private String title;
        private String content;
        private String imageUrl;
        private Integer rating;
        private Integer noiseLevel;
        private Integer safetyLevel;
        private Integer convenienceLevel;
        private Integer managementLevel;
        private String pros;
        private String cons;
        private String livingPeriod;
    }

    @lombok.Data
    public static class UpdateProfileRequest {
        private String nickname;
        private String profileImage;
        private String characterImage;
        private String location;
        private String university;
        private String major;
        private String introduction;
    }
}
