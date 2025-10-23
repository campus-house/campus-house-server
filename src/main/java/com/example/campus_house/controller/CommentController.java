package com.example.campus_house.controller;

import com.example.campus_house.entity.Comment;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.CommentService;
import com.example.campus_house.service.AuthService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentController {
    
    private final CommentService commentService;
    private final AuthService authService;
    
    // 댓글 작성
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Comment comment = commentService.createComment(
                    postId,
                    user.getUserId(),
                    request.getContent(),
                    request.getParentId()
            );
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 대댓글 작성
    @Operation(summary = "대댓글 작성", description = "댓글에 대댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/comments/{parentCommentId}/replies")
    public ResponseEntity<Comment> createReply(
            @Parameter(description = "부모 댓글 ID", required = true)
            @PathVariable Long parentCommentId,
            @RequestBody CreateCommentRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Comment comment = commentService.createComment(
                    request.getPostId(),
                    user.getUserId(),
                    request.getContent(),
                    parentCommentId
            );
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 댓글 목록 조회
    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByPostId(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 댓글 수정
    @Operation(summary = "댓글 수정", description = "내가 작성한 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Comment comment = commentService.updateComment(
                    commentId,
                    user.getUserId(),
                    request.getContent()
            );
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "내가 작성한 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            commentService.deleteComment(commentId, user.getUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTO 클래스들
    public static class CreateCommentRequest {
        private Long postId;
        private String content;
        private Long parentId;
        
        // Getters and Setters
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
    }
    
    public static class UpdateCommentRequest {
        private String content;
        
        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}