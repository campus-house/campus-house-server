package com.example.campus_house.controller;

import com.example.campus_house.entity.Bookmark;
import com.example.campus_house.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    
    // 게시글 북마크 토글
    @PostMapping("/post/{postId}")
    public ResponseEntity<BookmarkResponse> togglePostBookmark(
            @PathVariable Long postId,
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
    @GetMapping("/post/{postId}/status")
    public ResponseEntity<Boolean> isPostBookmarked(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        try {
            boolean isBookmarked = bookmarkService.isPostBookmarked(postId, userId);
            return ResponseEntity.ok(isBookmarked);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 사용자의 북마크한 게시글 조회 (페이징)
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Bookmark>> getBookmarkedPostsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Bookmark> bookmarks = bookmarkService.getBookmarkedPostsByUserId(userId, pageable);
        return ResponseEntity.ok(bookmarks);
    }
    
    // 사용자의 북마크한 게시글 조회 (페이징 없음)
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<Bookmark>> getAllBookmarkedPostsByUserId(@PathVariable Long userId) {
        List<Bookmark> bookmarks = bookmarkService.getAllBookmarkedPostsByUserId(userId);
        return ResponseEntity.ok(bookmarks);
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
}
