package com.example.campus_house.repository;

import com.example.campus_house.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    // 사용자가 특정 게시글을 북마크했는지 확인
    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);
    
    // 사용자가 특정 게시글을 북마크했는지 확인 (boolean)
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // 사용자의 북마크 목록 조회 (페이징)
    @Query("SELECT b FROM Bookmark b JOIN FETCH b.post WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자의 북마크 목록 조회 (페이징 없음)
    @Query("SELECT b FROM Bookmark b JOIN FETCH b.post WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    // 특정 게시글의 북마크 삭제
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
