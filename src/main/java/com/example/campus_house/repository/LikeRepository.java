package com.example.campus_house.repository;

import com.example.campus_house.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    @Query("SELECT l FROM Like l WHERE l.user.userId = :userId AND l.post.id = :postId")
    Optional<Like> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
    
    // 특정 게시글의 좋아요 수 조회
    long countByPostId(Long postId);
    
    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인 (boolean)
    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.user.userId = :userId AND l.post.id = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
    
    // 특정 게시글의 좋아요 삭제
    @Query("DELETE FROM Like l WHERE l.user.userId = :userId AND l.post.id = :postId")
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
    
    // 특정 사용자의 좋아요한 게시글 조회
    @Query("SELECT l FROM Like l WHERE l.user.userId = :userId ORDER BY l.createdAt DESC")
    List<Like> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
