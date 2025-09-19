package com.example.campus_house.repository;

import com.example.campus_house.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
    
    long countByCommentId(Long commentId);
    
    List<CommentLike> findByCommentId(Long commentId);
    
    List<CommentLike> findByUserId(Long userId);
}
