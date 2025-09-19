package com.example.campus_house.repository;

import com.example.campus_house.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; // 현재 사용하지 않음
// import org.springframework.data.repository.query.Param; // 현재 사용하지 않음
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    
    // 특정 게시글의 좋아요 수 조회
    long countByPostId(Long postId);
    
    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인 (boolean)
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // 특정 게시글의 좋아요 삭제
    void deleteByUserIdAndPostId(Long userId, Long postId);
    
    // 특정 사용자의 좋아요한 게시글 조회
    List<Like> findByUserIdOrderByCreatedAtDesc(Long userId);
}
