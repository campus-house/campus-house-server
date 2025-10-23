package com.example.campus_house.service;

import com.example.campus_house.entity.Like;
import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.LikeRepository;
import com.example.campus_house.repository.PostRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final NotificationService notificationService;
    private final BadgeService badgeService;
    
    // 게시글 좋아요 토글
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 이미 좋아요를 눌렀는지 확인
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            // 좋아요 취소
            likeRepository.deleteByUserIdAndPostId(userId, postId);
            postService.updateLikeCount(postId, -1);
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            Like like = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            
            likeRepository.save(like);
            postService.updateLikeCount(postId, 1);
            badgeService.awardIfFirstLike(userId);
            
            // 게시글 작성자에게 좋아요 알림 전송 (자신의 게시글이 아닌 경우)
            if (!post.getAuthor().getUserId().equals(userId)) {
                notificationService.createNotification(
                        post.getAuthor().getUserId(),
                        com.example.campus_house.entity.Notification.NotificationType.POST_LIKE,
                        "게시글에 좋아요가 달렸습니다",
                        user.getNickname() + "님이 '" + post.getTitle() + "' 게시글에 좋아요를 눌렀습니다.",
                        postId.toString(),
                        "POST"
                );
            }
            
            return true; // 좋아요 추가됨
        }
    }
    
    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    public boolean isLiked(Long postId, Long userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }
}