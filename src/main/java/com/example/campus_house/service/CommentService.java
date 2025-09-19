package com.example.campus_house.service;

import com.example.campus_house.entity.Comment;
import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.CommentRepository;
import com.example.campus_house.repository.PostRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final NotificationService notificationService;
    
    // 게시글의 댓글 조회
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
    
    // 댓글 생성
    @Transactional
    public Comment createComment(Long postId, Long userId, String content, String imageUrl, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Comment parent = null;
        if (parentId != null) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
        }
        
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parent(parent)
                .content(content)
                .imageUrl(imageUrl)
                .status(Comment.CommentStatus.ACTIVE)
                .likeCount(0)
                .build();
        
        Comment savedComment = commentRepository.save(comment);
        
        // 게시글의 댓글 수 업데이트
        postService.updateCommentCount(postId);
        
        // 알림 전송
        if (parentId == null) {
            // 일반 댓글인 경우 - 게시글 작성자에게 알림
            if (!post.getUser().getId().equals(userId)) {
                notificationService.createNotificationFromUser(
                        post.getUser().getId(),
                        userId,
                        com.example.campus_house.entity.Notification.NotificationType.POST_COMMENT,
                        "게시글에 댓글이 달렸습니다",
                        user.getNickname() + "님이 '" + post.getTitle() + "' 게시글에 댓글을 남겼습니다.",
                        postId.toString(),
                        "POST"
                );
            }
        } else {
            // 대댓글인 경우 - 부모 댓글 작성자에게 알림
            if (parent != null && !parent.getUser().getId().equals(userId)) {
                notificationService.createNotificationFromUser(
                        parent.getUser().getId(),
                        userId,
                        com.example.campus_house.entity.Notification.NotificationType.COMMENT_REPLY,
                        "댓글에 답글이 달렸습니다",
                        user.getNickname() + "님이 댓글에 답글을 남겼습니다.",
                        savedComment.getId().toString(),
                        "COMMENT"
                );
            }
        }
        
        return savedComment;
    }
    
    // 댓글 수정
    @Transactional
    public Comment updateComment(Long commentId, Long userId, String content, String imageUrl) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        // 작성자만 수정 가능
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글을 수정할 권한이 없습니다.");
        }
        
        comment.setContent(content);
        comment.setImageUrl(imageUrl);
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }
    
    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        // 작성자만 삭제 가능
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }
        
        comment.setStatus(Comment.CommentStatus.DELETED);
        commentRepository.save(comment);
        
        // 게시글의 댓글 수 업데이트
        postService.updateCommentCount(comment.getPost().getId());
    }
    
    // 특정 사용자의 댓글 조회
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, Comment.CommentStatus.ACTIVE);
    }
    
    // 대댓글 조회
    public List<Comment> getRepliesByParentId(Long parentId) {
        return commentRepository.findByParentIdAndStatusOrderByCreatedAtAsc(parentId, Comment.CommentStatus.ACTIVE);
    }
    
    // 댓글 좋아요 수 업데이트
    @Transactional
    public void updateCommentLikeCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        comment.setLikeCount(comment.getLikes().size());
        commentRepository.save(comment);
    }
}
