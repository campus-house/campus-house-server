package com.example.campus_house.service;

import com.example.campus_house.entity.Comment;
import com.example.campus_house.entity.Notification;
import com.example.campus_house.entity.Post;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.CommentRepository;
import com.example.campus_house.repository.PostRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BadgeService badgeService;
    
    // 게시글의 댓글 조회 (최상위 댓글만)
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);
    }
    
    // 대댓글 조회
    public List<Comment> getRepliesByParentId(Long parentId) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(parentId);
    }
    
    // 댓글 생성
    @Transactional
    public Comment createComment(Long postId, Long userId, String content, Long parentId) {
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
                .author(user)
                .parent(parent)
                .content(content)
                .build();
        
        Comment savedComment = commentRepository.save(comment);
        
        // 댓글 수 증가
        postService.updateCommentCount(postId, 1);

        // 배지: 첫 댓글
        badgeService.awardIfFirstComment(userId);
        
        // 알림 생성 (게시글 작성자에게)
        if (!post.getAuthor().getUserId().equals(userId)) {
            notificationService.createNotification(
                    post.getAuthor().getUserId(),
                    Notification.NotificationType.POST_COMMENT,
                    "새로운 댓글이 달렸습니다.",
                    "게시글에 새로운 댓글이 달렸습니다.",
                    postId.toString(),
                    "POST"
            );
        }
        
        // 대댓글인 경우 부모 댓글 작성자에게도 알림
        if (parent != null && !parent.getAuthor().getUserId().equals(userId)) {
            notificationService.createNotification(
                    parent.getAuthor().getUserId(),
                    Notification.NotificationType.COMMENT_REPLY,
                    "댓글에 답글이 달렸습니다.",
                    "댓글에 답글이 달렸습니다.",
                    postId.toString(),
                    "POST"
            );
        }
        
        return savedComment;
    }

    // 특정 사용자의 댓글 목록 조회 (최신순)
    public List<Comment> getCommentsByAuthor(Long authorId) {
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }
    
    // 댓글 수정
    @Transactional
    public Comment updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        if (!comment.getAuthor().getUserId().equals(userId)) {
            throw new RuntimeException("댓글을 수정할 권한이 없습니다.");
        }
        
        comment.setContent(content);
        return commentRepository.save(comment);
    }
    
    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        if (!comment.getAuthor().getUserId().equals(userId)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }
        
        // 댓글 수 감소
        postService.updateCommentCount(comment.getPost().getId(), -1);
        
        commentRepository.delete(comment);
    }
}