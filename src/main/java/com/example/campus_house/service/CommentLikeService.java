package com.example.campus_house.service;

import com.example.campus_house.entity.CommentLike;
import com.example.campus_house.entity.Comment;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentLikeService {
    
    private final CommentLikeRepository commentLikeRepository;
    
    public boolean toggleLike(Long commentId, Long userId) {
        // 이미 좋아요가 있는지 확인
        boolean exists = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        
        if (exists) {
            // 좋아요 취소
            commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
            return false;
        } else {
            // 좋아요 추가
            CommentLike like = CommentLike.builder()
                    .comment(Comment.builder().id(commentId).build())
                    .user(User.builder().id(userId).build())
                    .build();
            commentLikeRepository.save(like);
            return true;
        }
    }
    
    public boolean isLiked(Long commentId, Long userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }
    
    public long getLikeCount(Long commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }
}
