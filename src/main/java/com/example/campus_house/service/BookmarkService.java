package com.example.campus_house.service;

import com.example.campus_house.entity.Bookmark;
import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.BookmarkRepository;
import com.example.campus_house.repository.PostRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {
    
    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    // 게시글 북마크 토글 (간단한 버전)
    @Transactional
    public boolean toggleBookmark(Long postId, Long userId) {
        return togglePostBookmark(postId, userId);
    }
    
    // 게시글 북마크 토글
    @Transactional
    public boolean togglePostBookmark(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 이미 북마크했는지 확인
        if (bookmarkRepository.existsByUserIdAndPostId(userId, postId)) {
            // 북마크 취소
            bookmarkRepository.deleteByUserIdAndPostId(userId, postId);
            return false; // 북마크 취소됨
        } else {
            // 북마크 추가
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .post(post)
                    .build();
            
            bookmarkRepository.save(bookmark);
            return true; // 북마크 추가됨
        }
    }
    
    // 게시글 북마크 상태 확인
    public boolean isPostBookmarked(Long postId, Long userId) {
        return bookmarkRepository.existsByUserIdAndPostId(userId, postId);
    }
    
    // 사용자의 북마크한 게시글 조회
    public Page<Bookmark> getBookmarkedPostsByUserId(Long userId, Pageable pageable) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    // 특정 사용자의 북마크한 게시글 목록 조회 (페이징 없음)
    public List<Bookmark> getAllBookmarkedPostsByUserId(Long userId) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
