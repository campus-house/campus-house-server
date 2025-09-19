package com.example.campus_house.repository;

import com.example.campus_house.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 게시글의 댓글 조회 (대댓글 포함)
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.status = 'ACTIVE' ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    // 게시글의 댓글 수 조회
    long countByPostIdAndStatus(Long postId, Comment.CommentStatus status);
    
    // 특정 사용자의 댓글 조회
    List<Comment> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Comment.CommentStatus status);
    
    // 대댓글 조회
    List<Comment> findByParentIdAndStatusOrderByCreatedAtAsc(Long parentId, Comment.CommentStatus status);
}
