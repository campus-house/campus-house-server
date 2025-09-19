package com.example.campus_house.service;

import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.PostRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    // private final NotificationService notificationService; // 현재 사용하지 않음
    
    // 게시판 카테고리별 게시글 조회
    public Page<Post> getPostsByBoardCategory(Post.BoardCategory boardCategory, Pageable pageable) {
        return postRepository.findByBoardCategoryAndStatusOrderByCreatedAtDesc(boardCategory, Post.PostStatus.ACTIVE, pageable);
    }
    
    // 게시판 카테고리와 타입별 게시글 조회 (접근 권한 확인 포함)
    public Page<Post> getPostsByBoardCategoryAndType(Post.BoardCategory boardCategory, Post.BoardType boardType, 
                                                   User user, Pageable pageable) {
        // 아파트 일반 게시판은 거주지 인증된 사용자만 접근 가능
        if (boardCategory == Post.BoardCategory.APARTMENT && boardType == Post.BoardType.GENERAL) {
            if (user == null || !user.getIsVerified()) {
                throw new RuntimeException("아파트 일반 게시판은 거주지 인증된 사용자만 접근할 수 있습니다.");
            }
        }
        
        return postRepository.findByBoardCategoryAndBoardTypeAndStatusOrderByCreatedAtDesc(
                boardCategory, boardType, Post.PostStatus.ACTIVE, pageable);
    }
    
    // 새 질문 조회 (24시간 이내)
    public List<Post> getNewQuestions() {
        return postRepository.findNewQuestions();
    }
    
    // 게시글 상세 조회
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 조회수 증가
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        
        return post;
    }
    
    // 게시글 생성
    @Transactional
    public Post createPost(Long userId, String title, String content, Post.BoardCategory boardCategory, 
                          Post.BoardType boardType, String location) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 아파트 일반 게시판은 거주지 인증된 사용자만 작성 가능
        if (boardCategory == Post.BoardCategory.APARTMENT && boardType == Post.BoardType.GENERAL && 
            !user.getIsVerified()) {
            throw new RuntimeException("아파트 일반 게시판은 거주지 인증된 사용자만 작성할 수 있습니다.");
        }
        
        // 동네 게시판에는 질문 게시판 없음
        if (boardCategory == Post.BoardCategory.NEIGHBORHOOD && boardType == Post.BoardType.QUESTION) {
            throw new RuntimeException("동네 게시판에는 질문 게시판이 없습니다.");
        }
        
        Post post = Post.builder()
                .user(user)
                .boardCategory(boardCategory)
                .boardType(boardType)
                .title(title)
                .content(content)
                .location(location)
                .status(Post.PostStatus.ACTIVE)
                .isNew(boardType == Post.BoardType.QUESTION) // 질문 게시판만 새 게시글 표시
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .build();
        
        return postRepository.save(post);
    }
    
    // 게시글 수정
    @Transactional
    public Post updatePost(Long postId, Long userId, String title, String content, String location) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 작성자만 수정 가능
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }
        
        post.setTitle(title);
        post.setContent(content);
        post.setLocation(location);
        post.setUpdatedAt(LocalDateTime.now());
        
        return postRepository.save(post);
    }
    
    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 작성자만 삭제 가능
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }
        
        post.setStatus(Post.PostStatus.DELETED);
        postRepository.save(post);
    }
    
    // 게시글 검색 (카테고리별, 접근 권한 확인 포함)
    public Page<Post> searchPosts(String keyword, Post.BoardCategory boardCategory, Post.BoardType boardType, 
                                 User user, Pageable pageable) {
        // 아파트 일반 게시판은 거주지 인증된 사용자만 접근 가능
        if (boardCategory == Post.BoardCategory.APARTMENT && boardType == Post.BoardType.GENERAL) {
            if (user == null || !user.getIsVerified()) {
                throw new RuntimeException("아파트 일반 게시판은 거주지 인증된 사용자만 접근할 수 있습니다.");
            }
        }
        
        return postRepository.findByKeywordAndBoardCategoryAndBoardType(keyword, boardCategory, boardType, pageable);
    }
    
    // 제목으로 검색 (카테고리별, 접근 권한 확인 포함)
    public Page<Post> searchPostsByTitle(String title, Post.BoardCategory boardCategory, Post.BoardType boardType, 
                                        User user, Pageable pageable) {
        // 아파트 일반 게시판은 거주지 인증된 사용자만 접근 가능
        if (boardCategory == Post.BoardCategory.APARTMENT && boardType == Post.BoardType.GENERAL) {
            if (user == null || !user.getIsVerified()) {
                throw new RuntimeException("아파트 일반 게시판은 거주지 인증된 사용자만 접근할 수 있습니다.");
            }
        }
        
        return postRepository.findByTitleContainingAndBoardCategoryAndBoardTypeAndStatusOrderByCreatedAtDesc(
                title, boardCategory, boardType, Post.PostStatus.ACTIVE, pageable);
    }
    
    // 내용으로 검색 (카테고리별, 접근 권한 확인 포함)
    public Page<Post> searchPostsByContent(String content, Post.BoardCategory boardCategory, Post.BoardType boardType, 
                                          User user, Pageable pageable) {
        // 아파트 일반 게시판은 거주지 인증된 사용자만 접근 가능
        if (boardCategory == Post.BoardCategory.APARTMENT && boardType == Post.BoardType.GENERAL) {
            if (user == null || !user.getIsVerified()) {
                throw new RuntimeException("아파트 일반 게시판은 거주지 인증된 사용자만 접근할 수 있습니다.");
            }
        }
        
        return postRepository.findByContentContainingAndBoardCategoryAndBoardTypeAndStatusOrderByCreatedAtDesc(
                content, boardCategory, boardType, Post.PostStatus.ACTIVE, pageable);
    }
    
    // 특정 사용자의 게시글 조회
    public Page<Post> getPostsByUserId(Long userId, Pageable pageable) {
        return postRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, Post.PostStatus.ACTIVE, pageable);
    }
    
    // 인기 게시글 조회 (카테고리별, 접근 권한 확인 포함)
    public Page<Post> getPopularPosts(Post.BoardCategory boardCategory, Post.BoardType boardType, 
                                     User user, Pageable pageable) {
        // 아파트 일반 게시판은 거주지 인증된 사용자만 접근 가능
        if (boardCategory == Post.BoardCategory.APARTMENT && boardType == Post.BoardType.GENERAL) {
            if (user == null || !user.getIsVerified()) {
                throw new RuntimeException("아파트 일반 게시판은 거주지 인증된 사용자만 접근할 수 있습니다.");
            }
        }
        
        return postRepository.findPopularPosts(boardCategory, boardType, pageable);
    }
    
    // 새 질문 표시 해제 (24시간 후 자동으로 해제되도록 스케줄러에서 호출)
    @Transactional
    public void markQuestionAsNotNew(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        post.setIsNew(false);
        postRepository.save(post);
    }
    
    // 댓글 수 업데이트
    @Transactional
    public void updateCommentCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 댓글 수는 CommentService에서 계산해서 전달받는 것이 좋지만, 
        // 간단하게 구현하기 위해 여기서 처리
        post.setCommentCount(post.getComments().size());
        postRepository.save(post);
    }
    
    // 좋아요 수 업데이트
    @Transactional
    public void updateLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 좋아요 수는 LikeService에서 계산해서 전달받는 것이 좋지만,
        // 간단하게 구현하기 위해 여기서 처리
        post.setLikeCount(post.getLikes().size());
        postRepository.save(post);
    }
}
