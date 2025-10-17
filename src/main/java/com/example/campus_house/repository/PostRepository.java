package com.example.campus_house.repository;

import com.example.campus_house.entity.BoardType;
import com.example.campus_house.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 게시판 타입별 게시글 조회
    Page<Post> findByBoardTypeOrderByCreatedAtDesc(BoardType boardType, Pageable pageable);
    
    // 제목으로 검색
    Page<Post> findByTitleContainingAndBoardTypeOrderByCreatedAtDesc(String title, BoardType boardType, Pageable pageable);
    
    // 내용으로 검색
    Page<Post> findByContentContainingAndBoardTypeOrderByCreatedAtDesc(String content, BoardType boardType, Pageable pageable);
    
    // 제목과 내용으로 통합 검색
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.boardType = :boardType ORDER BY p.createdAt DESC")
    Page<Post> findByKeywordAndBoardType(@Param("keyword") String keyword, @Param("boardType") BoardType boardType, Pageable pageable);
    
    // 특정 사용자의 게시글 조회
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // 인기 게시글 조회 (좋아요 수 기준)
    @Query("SELECT p FROM Post p WHERE p.boardType = :boardType ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(@Param("boardType") BoardType boardType, Pageable pageable);
    
    // 조회수 기준 인기 게시글
    @Query("SELECT p FROM Post p WHERE p.boardType = :boardType ORDER BY p.viewCount DESC, p.createdAt DESC")
    Page<Post> findPopularPostsByViewCount(@Param("boardType") BoardType boardType, Pageable pageable);
}