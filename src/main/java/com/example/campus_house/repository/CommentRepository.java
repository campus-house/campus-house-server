package com.example.campus_house.repository;

import com.example.campus_house.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 게시글의 최상위 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    // 대댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId ORDER BY c.createdAt ASC")
    List<Comment> findByParentIdOrderByCreatedAtAsc(@Param("parentId") Long parentId);
    
    // 게시글의 댓글 수 조회
    long countByPostId(Long postId);
    
    // 특정 사용자의 댓글 조회
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}