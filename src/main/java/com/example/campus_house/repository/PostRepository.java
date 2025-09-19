package com.example.campus_house.repository;

import com.example.campus_house.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 게시판 카테고리별 게시글 조회
    Page<Post> findByBoardCategoryAndStatusOrderByCreatedAtDesc(Post.BoardCategory boardCategory, Post.PostStatus status, Pageable pageable);
    
    // 게시판 카테고리와 타입별 게시글 조회
    Page<Post> findByBoardCategoryAndBoardTypeAndStatusOrderByCreatedAtDesc(Post.BoardCategory boardCategory, Post.BoardType boardType, Post.PostStatus status, Pageable pageable);
    
    // 새 질문 조회 (24시간 이내) - 아파트 게시판만
    @Query("SELECT p FROM Post p WHERE p.boardCategory = 'APARTMENT' AND p.boardType = 'QUESTION' AND p.status = 'ACTIVE' AND p.isNew = true ORDER BY p.createdAt DESC")
    List<Post> findNewQuestions();
    
    // 제목으로 검색 (카테고리별)
    Page<Post> findByTitleContainingAndBoardCategoryAndBoardTypeAndStatusOrderByCreatedAtDesc(String title, Post.BoardCategory boardCategory, Post.BoardType boardType, Post.PostStatus status, Pageable pageable);
    
    // 내용으로 검색 (카테고리별)
    Page<Post> findByContentContainingAndBoardCategoryAndBoardTypeAndStatusOrderByCreatedAtDesc(String content, Post.BoardCategory boardCategory, Post.BoardType boardType, Post.PostStatus status, Pageable pageable);
    
    // 제목과 내용으로 통합 검색 (카테고리별)
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.boardCategory = :boardCategory AND p.boardType = :boardType AND p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    Page<Post> findByKeywordAndBoardCategoryAndBoardType(@Param("keyword") String keyword, @Param("boardCategory") Post.BoardCategory boardCategory, @Param("boardType") Post.BoardType boardType, Pageable pageable);
    
    // 특정 사용자의 게시글 조회
    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Post.PostStatus status, Pageable pageable);
    
    // 인기 게시글 조회 (좋아요 수 기준) - 카테고리별
    @Query("SELECT p FROM Post p WHERE p.boardCategory = :boardCategory AND p.boardType = :boardType AND p.status = 'ACTIVE' ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(@Param("boardCategory") Post.BoardCategory boardCategory, @Param("boardType") Post.BoardType boardType, Pageable pageable);
    
    // 24시간 이전에 생성된 새 질문 조회 (아파트 게시판만)
    List<Post> findByBoardCategoryAndBoardTypeAndStatusAndIsNewAndCreatedAtBefore(Post.BoardCategory boardCategory, Post.BoardType boardType, Post.PostStatus status, Boolean isNew, LocalDateTime createdAt);
}
