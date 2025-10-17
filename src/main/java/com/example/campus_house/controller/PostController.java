package com.example.campus_house.controller;

import com.example.campus_house.dto.ApiResponse;
import com.example.campus_house.dto.QuestionRequest;
import com.example.campus_house.entity.BoardType;
import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.PostService;
import com.example.campus_house.service.LikeService;
import com.example.campus_house.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "게시글", description = "게시글 관련 API")
public class PostController {
    
    private final PostService postService;
    private final AuthService authService;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;
    
    // 게시글 작성
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "거주지 인증 필요")
    })
    @PostMapping("/boards/{type}/posts")
    public ResponseEntity<ApiResponse<Post>> createPost(
            @Parameter(description = "게시판 타입 (APARTMENT, QUESTION, LOCAL)", required = true)
            @PathVariable String type,
            @RequestBody Post post,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            BoardType boardType = BoardType.valueOf(type.toUpperCase());
            
            // APARTMENT와 QUESTION 게시판은 거주지 인증 필요
            postService.checkBoardAccessPermission(user.getId(), boardType);
            
            post.setAuthor(user);
            post.setBoardType(boardType);
            Post createdPost = postService.createPost(post);
            return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 작성되었습니다.", createdPost));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // 모든 게시글 조회 (페이징)
    @Operation(summary = "게시글 목록 조회", description = "특정 게시판의 모든 게시글을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "거주지 인증 필요")
    })
    @GetMapping("/boards/{type}/posts")
    public ResponseEntity<Page<Post>> getPostsByBoardType(
            @Parameter(description = "게시판 타입 (APARTMENT, QUESTION, LOCAL)", required = true)
            @PathVariable String type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            BoardType boardType = BoardType.valueOf(type.toUpperCase());
            
            // APARTMENT와 QUESTION 게시판은 거주지 인증 필요
            if (boardType == BoardType.APARTMENT || boardType == BoardType.QUESTION) {
                if (token == null) {
                    return ResponseEntity.status(401).build();
                }
                
                try {
                    User user = authService.getUserFromToken(token.substring(7));
                    postService.checkBoardAccessPermission(user.getId(), boardType);
                    
                    // QUESTION 게시판인 경우 거주지 기반 필터링 적용
                    if (boardType == BoardType.QUESTION) {
                        Page<Post> posts = postService.getQuestionsForResident(user.getId(), pageable);
                        return ResponseEntity.ok(posts);
                    }
                    
                    // APARTMENT 게시판인 경우 거주지 기반 필터링 적용
                    if (boardType == BoardType.APARTMENT) {
                        Page<Post> posts = postService.getApartmentPostsForResident(user.getId(), pageable);
                        return ResponseEntity.ok(posts);
                    }
                    
                    // 기타 게시판은 일반 조회
                    Page<Post> posts = postService.getPostsByBoardType(boardType, pageable);
                    return ResponseEntity.ok(posts);
                } catch (RuntimeException e) {
                    return ResponseEntity.status(403).build();
                }
            }
            
            // LOCAL과 TRANSFER 게시판은 인증 불필요
            Page<Post> posts = postService.getPostsByBoardType(boardType, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 게시글 조회
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            postService.incrementViewCount(id); // 조회수 증가
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 게시글 수정
    @Operation(summary = "게시글 수정", description = "내가 작성한 게시글을 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long id,
            @RequestBody Post updatedPost,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Post existingPost = postService.getPostById(id);
            
            // 작성자만 수정 가능
            if (!existingPost.getAuthor().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            Post result = postService.updatePost(id, updatedPost);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "내가 작성한 게시글을 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Post existingPost = postService.getPostById(id);
            
            // 작성자만 삭제 가능
            if (!existingPost.getAuthor().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 최신순 조회
    @Operation(summary = "최신순 게시글 조회", description = "특정 게시판의 최신 게시글을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/boards/{type}/posts/latest")
    public ResponseEntity<Page<Post>> getLatestPosts(
            @Parameter(description = "게시판 타입 (APARTMENT, QUESTION, LOCAL)", required = true)
            @PathVariable String type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            BoardType boardType = BoardType.valueOf(type.toUpperCase());
            Page<Post> posts = postService.getPostsByBoardType(boardType, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 인기순 조회
    @Operation(summary = "인기순 게시글 조회", description = "좋아요 수 기준으로 인기 게시글을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/boards/{type}/posts/popular")
    public ResponseEntity<Page<Post>> getPopularPosts(
            @Parameter(description = "게시판 타입 (APARTMENT, QUESTION, LOCAL)", required = true)
            @PathVariable String type,
            @PageableDefault(size = 20, sort = "likeCount", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            BoardType boardType = BoardType.valueOf(type.toUpperCase());
            Page<Post> posts = postService.getPopularPosts(boardType, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 검색
    @Operation(summary = "게시글 검색", description = "제목과 내용에서 키워드를 검색합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/boards/{type}/posts/search")
    public ResponseEntity<Page<Post>> searchPosts(
            @Parameter(description = "게시판 타입 (APARTMENT, QUESTION, LOCAL)", required = true)
            @PathVariable String type,
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            BoardType boardType = BoardType.valueOf(type.toUpperCase());
            Page<Post> posts = postService.searchPosts(keyword, boardType, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 좋아요 토글
    @Operation(summary = "게시글 좋아요 토글", description = "게시글에 좋아요를 추가하거나 제거합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<String> toggleLike(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            boolean isLiked = likeService.toggleLike(postId, user.getId());
            return ResponseEntity.ok(isLiked ? "좋아요 추가됨" : "좋아요 취소됨");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 북마크 토글
    @Operation(summary = "게시글 북마크 토글", description = "게시글을 북마크에 추가하거나 제거합니다. 게시글의 스크랩 수가 변경됩니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "북마크 토글 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PostMapping("/posts/{postId}/bookmark")
    public ResponseEntity<BookmarkResponse> toggleBookmark(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        try {
            boolean isBookmarked = bookmarkService.togglePostBookmark(postId, userId);
            BookmarkResponse response = new BookmarkResponse(isBookmarked);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 북마크 상태 확인
    @Operation(summary = "게시글 북마크 상태 확인", description = "특정 게시글이 북마크되어 있는지 확인합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "북마크 상태 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/posts/{postId}/bookmark/status")
    public ResponseEntity<Boolean> isBookmarked(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        try {
            boolean isBookmarked = bookmarkService.isPostBookmarked(postId, userId);
            return ResponseEntity.ok(isBookmarked);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTO 클래스
    public static class BookmarkResponse {
        private boolean isBookmarked;
        
        public BookmarkResponse(boolean isBookmarked) {
            this.isBookmarked = isBookmarked;
        }
        
        // Getters and Setters
        public boolean isBookmarked() { return isBookmarked; }
        public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }
    }
    
    // ========== 건물별 질문 관련 API ==========
    
    // 건물별 질문 목록 조회 (QUESTION 게시판에서)
    @Operation(summary = "건물별 질문 목록 조회", description = "특정 건물에 대한 질문들을 조회합니다.")
    @GetMapping("/buildings/{buildingId}/questions")
    public ResponseEntity<Page<Post>> getBuildingQuestions(
            @Parameter(description = "건물 ID", required = true)
            @PathVariable Long buildingId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> questions = postService.getQuestionsByBuildingId(buildingId, pageable);
        return ResponseEntity.ok(questions);
    }
    
    // 건물별 질문 작성 (QUESTION 게시판에)
    @Operation(summary = "건물별 질문 작성", description = "특정 건물에 대한 질문을 작성합니다.")
    @PostMapping("/buildings/{buildingId}/questions")
    public ResponseEntity<Post> createBuildingQuestion(
            @Parameter(description = "건물 ID", required = true)
            @PathVariable Long buildingId,
            @RequestHeader("Authorization") String token,
            @RequestBody QuestionRequest request) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Post question = postService.createBuildingQuestion(user.getId(), buildingId, request.getTitle(), request.getContent());
            return ResponseEntity.ok(question);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 건물별 질문 수 조회
    @Operation(summary = "건물별 질문 수 조회", description = "특정 건물에 대한 질문 수를 조회합니다.")
    @GetMapping("/buildings/{buildingId}/questions/count")
    public ResponseEntity<Long> getBuildingQuestionCount(
            @Parameter(description = "건물 ID", required = true)
            @PathVariable Long buildingId) {
        Long count = postService.getQuestionCountByBuildingId(buildingId);
        return ResponseEntity.ok(count);
    }
    
    // 거주지 기반 질문 수 조회 (QUESTION 게시판용)
    @Operation(summary = "거주지 기반 질문 수 조회", description = "사용자가 거주 중인 건물의 질문 수를 조회합니다.")
    @GetMapping("/boards/question/posts/count")
    public ResponseEntity<Long> getQuestionCountForResident(
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Long count = postService.getQuestionCountForResident(user.getId());
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 거주지 기반 APARTMENT 게시글 수 조회
    @Operation(summary = "거주지 기반 APARTMENT 게시글 수 조회", description = "사용자가 거주 중인 건물의 APARTMENT 게시글 수를 조회합니다.")
    @GetMapping("/boards/apartment/posts/count")
    public ResponseEntity<Long> getApartmentPostCountForResident(
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Long count = postService.getApartmentPostCountForResident(user.getId());
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}