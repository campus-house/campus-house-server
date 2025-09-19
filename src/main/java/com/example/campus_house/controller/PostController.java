package com.example.campus_house.controller;

import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.PostService;
import com.example.campus_house.service.LikeService;
import com.example.campus_house.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement; // 현재 사용하지 않음
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "게시글", description = "게시글 관련 API")
public class PostController {
    
    private final PostService postService;
    private final AuthService authService;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;
    
    // 게시판 카테고리별 게시글 조회
    @Operation(summary = "게시판 카테고리별 게시글 조회", description = "동네 게시판 또는 아파트 게시판의 게시글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/board/{boardCategory}")
    public ResponseEntity<Page<Post>> getPostsByBoardCategory(
            @Parameter(description = "게시판 카테고리 (NEIGHBORHOOD, APARTMENT)", required = true)
            @PathVariable String boardCategory,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Post.BoardCategory category = Post.BoardCategory.valueOf(boardCategory.toUpperCase());
            Page<Post> posts = postService.getPostsByBoardCategory(category, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시판 카테고리와 타입별 게시글 조회
    @GetMapping("/board/{boardCategory}/{boardType}")
    public ResponseEntity<Page<Post>> getPostsByBoardCategoryAndType(
            @PathVariable String boardCategory,
            @PathVariable String boardType,
            @RequestHeader(value = "Authorization", required = false) String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Post.BoardCategory category = Post.BoardCategory.valueOf(boardCategory.toUpperCase());
            Post.BoardType type = Post.BoardType.valueOf(boardType.toUpperCase());
            
            // 사용자 정보 확인 (토큰이 있는 경우)
            User user = null;
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    user = authService.getUserFromToken(token.substring(7));
                } catch (Exception e) {
                    // 토큰이 유효하지 않으면 null 사용
                }
            }
            
            Page<Post> posts = postService.getPostsByBoardCategoryAndType(category, type, user, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); // 접근 권한 없음
        }
    }
    
    // 새 질문 조회
    @GetMapping("/questions/new")
    public ResponseEntity<List<Post>> getNewQuestions() {
        List<Post> questions = postService.getNewQuestions();
        return ResponseEntity.ok(questions);
    }
    
    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        try {
            Post post = postService.getPostById(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 게시글 생성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request) {
        try {
            Post post = postService.createPost(
                    request.getUserId(),
                    request.getTitle(),
                    request.getContent(),
                    request.getBoardCategory(),
                    request.getBoardType(),
                    request.getLocation()
            );
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request) {
        try {
            Post post = postService.updatePost(
                    postId,
                    request.getUserId(),
                    request.getTitle(),
                    request.getContent(),
                    request.getLocation()
            );
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @RequestParam Long userId) {
        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<Page<Post>> searchPosts(
            @RequestParam String keyword,
            @RequestParam String boardCategory,
            @RequestParam String boardType,
            @RequestHeader(value = "Authorization", required = false) String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Post.BoardCategory category = Post.BoardCategory.valueOf(boardCategory.toUpperCase());
            Post.BoardType type = Post.BoardType.valueOf(boardType.toUpperCase());
            
            // 사용자 정보 확인 (토큰이 있는 경우)
            User user = null;
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    user = authService.getUserFromToken(token.substring(7));
                } catch (Exception e) {
                    // 토큰이 유효하지 않으면 null 사용
                }
            }
            
            Page<Post> posts = postService.searchPosts(keyword, category, type, user, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); // 접근 권한 없음
        }
    }
    
    // 제목으로 검색
    @GetMapping("/search/title")
    public ResponseEntity<Page<Post>> searchPostsByTitle(
            @RequestParam String title,
            @RequestParam String boardCategory,
            @RequestParam String boardType,
            @RequestHeader(value = "Authorization", required = false) String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Post.BoardCategory category = Post.BoardCategory.valueOf(boardCategory.toUpperCase());
            Post.BoardType type = Post.BoardType.valueOf(boardType.toUpperCase());
            
            // 사용자 정보 확인 (토큰이 있는 경우)
            User user = null;
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    user = authService.getUserFromToken(token.substring(7));
                } catch (Exception e) {
                    // 토큰이 유효하지 않으면 null 사용
                }
            }
            
            Page<Post> posts = postService.searchPostsByTitle(title, category, type, user, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); // 접근 권한 없음
        }
    }
    
    // 내용으로 검색
    @GetMapping("/search/content")
    public ResponseEntity<Page<Post>> searchPostsByContent(
            @RequestParam String content,
            @RequestParam String boardCategory,
            @RequestParam String boardType,
            @RequestHeader(value = "Authorization", required = false) String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Post.BoardCategory category = Post.BoardCategory.valueOf(boardCategory.toUpperCase());
            Post.BoardType type = Post.BoardType.valueOf(boardType.toUpperCase());
            
            // 사용자 정보 확인 (토큰이 있는 경우)
            User user = null;
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    user = authService.getUserFromToken(token.substring(7));
                } catch (Exception e) {
                    // 토큰이 유효하지 않으면 null 사용
                }
            }
            
            Page<Post> posts = postService.searchPostsByContent(content, category, type, user, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); // 접근 권한 없음
        }
    }
    
    // 특정 사용자의 게시글 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Post>> getPostsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> posts = postService.getPostsByUserId(userId, pageable);
        return ResponseEntity.ok(posts);
    }
    
    // 인기 게시글 조회
    @GetMapping("/popular/{boardCategory}/{boardType}")
    public ResponseEntity<Page<Post>> getPopularPosts(
            @PathVariable String boardCategory,
            @PathVariable String boardType,
            @RequestHeader(value = "Authorization", required = false) String token,
            @PageableDefault(size = 20, sort = "likeCount", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Post.BoardCategory category = Post.BoardCategory.valueOf(boardCategory.toUpperCase());
            Post.BoardType type = Post.BoardType.valueOf(boardType.toUpperCase());
            
            // 사용자 정보 확인 (토큰이 있는 경우)
            User user = null;
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    user = authService.getUserFromToken(token.substring(7));
                } catch (Exception e) {
                    // 토큰이 유효하지 않으면 null 사용
                }
            }
            
            Page<Post> posts = postService.getPopularPosts(category, type, user, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); // 접근 권한 없음
        }
    }
    
    // 게시글 좋아요 토글
    @Operation(summary = "게시글 좋아요 토글", description = "게시글에 좋아요를 추가하거나 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> toggleLike(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            boolean isLiked = likeService.toggleLike(postId, user.getId());
            String message = isLiked ? "좋아요를 추가했습니다." : "좋아요를 취소했습니다.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("좋아요 처리 중 오류가 발생했습니다.");
        }
    }
    
    // 게시글 북마크 토글
    @Operation(summary = "게시글 북마크 토글", description = "게시글을 북마크에 추가하거나 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 토글 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<String> toggleBookmark(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            boolean isBookmarked = bookmarkService.toggleBookmark(postId, user.getId());
            String message = isBookmarked ? "북마크에 추가했습니다." : "북마크에서 제거했습니다.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("북마크 처리 중 오류가 발생했습니다.");
        }
    }
    
    // DTO 클래스들
    public static class CreatePostRequest {
        private Long userId;
        private String title;
        private String content;
        private Post.BoardCategory boardCategory;
        private Post.BoardType boardType;
        private String location;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Post.BoardCategory getBoardCategory() { return boardCategory; }
        public void setBoardCategory(Post.BoardCategory boardCategory) { this.boardCategory = boardCategory; }
        public Post.BoardType getBoardType() { return boardType; }
        public void setBoardType(Post.BoardType boardType) { this.boardType = boardType; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }
    
    public static class UpdatePostRequest {
        private Long userId;
        private String title;
        private String content;
        private String location;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }
}
