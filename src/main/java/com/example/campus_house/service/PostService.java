package com.example.campus_house.service;

import com.example.campus_house.entity.BoardType;
import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.Building;
import com.example.campus_house.entity.User;
import com.example.campus_house.entity.Notification;
import com.example.campus_house.repository.PostRepository;
import com.example.campus_house.repository.BuildingRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    
    private final PostRepository postRepository;
    private final BadgeService badgeService;
    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    // 게시판 타입별 게시글 조회
    public Page<Post> getPostsByBoardType(BoardType boardType, Pageable pageable) {
        return postRepository.findByBoardTypeOrderByCreatedAtDesc(boardType, pageable);
    }
    
    // 게시글 상세 조회
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }
    
    // 게시글 생성
    @Transactional
    public Post createPost(Post post) {
        Post saved = postRepository.save(post);
        if (post.getAuthor() != null && post.getAuthor().getUserId() != null) {
            badgeService.awardIfFirstPost(post.getAuthor().getUserId());
        }
        return saved;
    }
    
    // 게시글 수정
    @Transactional
    public Post updatePost(Long postId, Post updatedPost) {
        Post post = getPostById(postId);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setImageUrl(updatedPost.getImageUrl());
        post.setBoardType(updatedPost.getBoardType());
        return postRepository.save(post);
    }
    
    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
    
    // 제목으로 검색
    public Page<Post> searchPostsByTitle(String title, BoardType boardType, Pageable pageable) {
        return postRepository.findByTitleContainingAndBoardTypeOrderByCreatedAtDesc(title, boardType, pageable);
    }
    
    // 내용으로 검색
    public Page<Post> searchPostsByContent(String content, BoardType boardType, Pageable pageable) {
        return postRepository.findByContentContainingAndBoardTypeOrderByCreatedAtDesc(content, boardType, pageable);
    }
    
    // 제목과 내용으로 통합 검색
    public Page<Post> searchPosts(String keyword, BoardType boardType, Pageable pageable) {
        return postRepository.findByKeywordAndBoardType(keyword, boardType, pageable);
    }
    
    // 특정 사용자의 게시글 조회
    public Page<Post> getPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
    }
    
    // 인기 게시글 조회 (좋아요 수 기준)
    public Page<Post> getPopularPosts(BoardType boardType, Pageable pageable) {
        return postRepository.findPopularPosts(boardType, pageable);
    }
    
    // 조회수 기준 인기 게시글
    public Page<Post> getPopularPostsByViewCount(BoardType boardType, Pageable pageable) {
        return postRepository.findPopularPostsByViewCount(boardType, pageable);
    }
    
    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = getPostById(postId);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }
    
    // 좋아요 수 업데이트
    @Transactional
    public void updateLikeCount(Long postId, int delta) {
        Post post = getPostById(postId);
        post.setLikeCount(Math.max(0, post.getLikeCount() + delta));
        postRepository.save(post);
    }
    
    // 북마크 수 업데이트
    @Transactional
    public void updateBookmarkCount(Long postId, int delta) {
        Post post = getPostById(postId);
        post.setBookmarkCount(Math.max(0, post.getBookmarkCount() + delta));
        postRepository.save(post);
    }
    
    // 댓글 수 업데이트
    @Transactional
    public void updateCommentCount(Long postId, int delta) {
        Post post = getPostById(postId);
        post.setCommentCount(Math.max(0, post.getCommentCount() + delta));
        postRepository.save(post);
    }
    
    // ========== 건물별 질문 관련 메서드 ==========
    
    // 건물별 질문 게시글 조회
    public Page<Post> getQuestionsByBuildingId(Long buildingId, Pageable pageable) {
        return postRepository.findByBuildingIdAndBoardTypeOrderByCreatedAtDesc(buildingId, BoardType.QUESTION, pageable);
    }
    
    // 건물별 질문 게시글 수
    public Long getQuestionCountByBuildingId(Long buildingId) {
        return postRepository.countByBuildingIdAndBoardType(buildingId, BoardType.QUESTION);
    }
    
    // 특정 사용자의 건물별 질문 게시글 조회
    public Page<Post> getQuestionsByUserIdAndBuildingId(Long userId, Long buildingId, Pageable pageable) {
        return postRepository.findByAuthorIdAndBuildingIdAndBoardTypeOrderByCreatedAtDesc(userId, buildingId, BoardType.QUESTION, pageable);
    }
    
    // 건물별 질문 작성
    @Transactional
    public Post createBuildingQuestion(Long userId, Long buildingId, String title, String content) {
        // 건물 존재 확인
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Post question = Post.builder()
                .title(title)
                .content(content)
                .boardType(BoardType.QUESTION)
                .author(user)
                .building(building)
                .likeCount(0)
                .bookmarkCount(0)
                .commentCount(0)
                .viewCount(0)
                .scrapCount(0)
                .build();
        
        Post saved = postRepository.save(question);
        
        // 첫 게시글 작성 시 배지 수여
        if (user.getUserId() != null) {
            badgeService.awardIfFirstPost(user.getUserId());
        }
        
        // 건물 거주자들에게 새 질문 알림 전송
        try {
            notificationService.notifyBuildingResidents(
                buildingId,
                userId,
                Notification.NotificationType.BUILDING_QUESTION,
                "새로운 건물 질문이 등록되었습니다",
                String.format("[%s] %s", building.getBuildingName(), title),
                saved.getId().toString(),
                "POST"
            );
        } catch (Exception e) {
            // 알림 전송 실패는 게시글 작성에 영향을 주지 않도록 예외 처리
            System.err.println("건물 질문 알림 전송 실패: " + e.getMessage());
        }
        
        return saved;
    }
    
    // ========== 거주지 인증 권한 체크 ==========
    
    // 거주지 인증 여부 확인
    public void checkResidenceVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (!user.getIsVerified() || user.getVerifiedBuildingId() == null) {
            throw new RuntimeException("거주지 인증이 필요한 서비스입니다. 거주지 인증을 먼저 완료해주세요.");
        }
    }
    
    // ========== 거주지 기반 질문 필터링 ==========
    
    // 사용자가 거주 중인 건물의 질문만 조회 (QUESTION 게시판용)
    public Page<Post> getQuestionsForResident(Long userId, Pageable pageable) {
        // 거주지 인증 여부 확인
        checkResidenceVerification(userId);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 인증된 건물의 질문만 조회
        return postRepository.findByBuildingIdAndBoardTypeOrderByCreatedAtDesc(
                user.getVerifiedBuildingId(), BoardType.QUESTION, pageable);
    }
    
    // 사용자가 거주 중인 건물의 질문 수 조회
    public Long getQuestionCountForResident(Long userId) {
        // 거주지 인증 여부 확인
        checkResidenceVerification(userId);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 인증된 건물의 질문 수 조회
        return postRepository.countByBuildingIdAndBoardType(user.getVerifiedBuildingId(), BoardType.QUESTION);
    }
    
    // ========== APARTMENT 게시판 거주지 기반 필터링 ==========
    
    // 사용자가 거주 중인 건물의 APARTMENT 게시글만 조회
    public Page<Post> getApartmentPostsForResident(Long userId, Pageable pageable) {
        // 거주지 인증 여부 확인
        checkResidenceVerification(userId);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 인증된 건물의 APARTMENT 게시글만 조회
        return postRepository.findByBuildingIdAndBoardTypeOrderByCreatedAtDesc(
                user.getVerifiedBuildingId(), BoardType.APARTMENT, pageable);
    }
    
    // 사용자가 거주 중인 건물의 APARTMENT 게시글 수 조회
    public Long getApartmentPostCountForResident(Long userId) {
        // 거주지 인증 여부 확인
        checkResidenceVerification(userId);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 인증된 건물의 APARTMENT 게시글 수 조회
        return postRepository.countByBuildingIdAndBoardType(user.getVerifiedBuildingId(), BoardType.APARTMENT);
    }
    
    // ========== TRANSFER 게시판 관련 메서드 ==========
    
    // 건물별 TRANSFER 게시글 조회
    public Page<Post> getTransfersByBuildingId(Long buildingId, Pageable pageable) {
        return postRepository.findByBuildingIdAndBoardTypeOrderByCreatedAtDesc(buildingId, BoardType.TRANSFER, pageable);
    }
    
    // 건물별 TRANSFER 게시글 수 조회
    public Long getTransferCountByBuildingId(Long buildingId) {
        return postRepository.countByBuildingIdAndBoardType(buildingId, BoardType.TRANSFER);
    }
    
    // 건물별 TRANSFER 게시글 작성
    @Transactional
    public Post createBuildingTransfer(Long userId, Long buildingId, String title, String content) {
        // 건물 존재 확인
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Post transfer = Post.builder()
                .title(title)
                .content(content)
                .boardType(BoardType.TRANSFER)
                .author(user)
                .building(building)
                .likeCount(0)
                .bookmarkCount(0)
                .commentCount(0)
                .viewCount(0)
                .scrapCount(0)
                .build();
        
        Post saved = postRepository.save(transfer);
        
        // 첫 게시글 작성 시 배지 수여
        if (user.getUserId() != null) {
            badgeService.awardIfFirstPost(user.getUserId());
        }
        
        return saved;
    }
    
    // 게시판 타입별 접근 권한 체크
    public void checkBoardAccessPermission(Long userId, BoardType boardType) {
        // APARTMENT와 QUESTION 게시판은 거주지 인증 필요
        if (boardType == BoardType.APARTMENT || boardType == BoardType.QUESTION) {
            checkResidenceVerification(userId);
        }
        // LOCAL과 TRANSFER 게시판은 인증 불필요
    }
}