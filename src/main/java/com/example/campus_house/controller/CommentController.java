package com.example.campus_house.controller;

import com.example.campus_house.entity.Comment;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.CommentService;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.CommentLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentController {
    
    private final CommentService commentService;
    private final AuthService authService;
    private final CommentLikeService commentLikeService;
    
    // 게시글의 댓글 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    
    // 댓글 생성
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CreateCommentRequest request) {
        try {
            Comment comment = commentService.createComment(
                    request.getPostId(),
                    request.getUserId(),
                    request.getContent(),
                    request.getImageUrl(),
                    request.getParentId()
            );
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        try {
            Comment comment = commentService.updateComment(
                    commentId,
                    request.getUserId(),
                    request.getContent(),
                    request.getImageUrl()
            );
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 사용자의 댓글 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable Long userId) {
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }
    
    // 대댓글 조회
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Comment>> getRepliesByParentId(@PathVariable Long parentId) {
        List<Comment> replies = commentService.getRepliesByParentId(parentId);
        return ResponseEntity.ok(replies);
    }
    
    // 댓글 좋아요 토글
    @Operation(summary = "댓글 좋아요 토글", description = "댓글에 좋아요를 추가하거나 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> toggleLike(
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            boolean isLiked = commentLikeService.toggleLike(commentId, user.getId());
            String message = isLiked ? "댓글에 좋아요를 추가했습니다." : "댓글 좋아요를 취소했습니다.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 좋아요 처리 중 오류가 발생했습니다.");
        }
    }
    
    // DTO 클래스들
    public static class CreateCommentRequest {
        private Long postId;
        private Long userId;
        private String content;
        private String imageUrl;
        private Long parentId;
        
        // Getters and Setters
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
    }
    
    public static class UpdateCommentRequest {
        private Long userId;
        private String content;
        private String imageUrl;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
