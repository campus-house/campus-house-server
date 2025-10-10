package com.example.campus_house.service;

import com.example.campus_house.entity.BoardType;
import com.example.campus_house.entity.Post;
import com.example.campus_house.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    
    private final PostRepository postRepository;
    private final BadgeService badgeService;
    
    // 게시판 타입별 게시글 조회
    public Page<Post> getPostsByBoardType(BoardType boardType, Pageable pageable) {
        return postRepository.findByBoardTypeOrderByCreatedAtDesc(boardType, pageable);
    }
    
    // 게시글 상세 조회
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }
    
    // 게시글 생성
    @Transactional
    public Post createPost(Post post) {
        Post saved = postRepository.save(post);
        if (post.getAuthor() != null && post.getAuthor().getId() != null) {
            badgeService.awardIfFirstPost(post.getAuthor().getId());
        }
        return saved;
    }
    
    // 게시글 수정
    @Transactional
    public Post updatePost(Long postId, Post updatedPost) {
        Post post = getPostById(postId);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setImageUrl(updatedPost.getImageUrl());
        post.setBoardType(updatedPost.getBoardType());
        return postRepository.save(post);
    }
    
    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
    
    // 제목으로 검색
    public Page<Post> searchPostsByTitle(String title, BoardType boardType, Pageable pageable) {
        return postRepository.findByTitleContainingAndBoardTypeOrderByCreatedAtDesc(title, boardType, pageable);
    }
    
    // 내용으로 검색
    public Page<Post> searchPostsByContent(String content, BoardType boardType, Pageable pageable) {
        return postRepository.findByContentContainingAndBoardTypeOrderByCreatedAtDesc(content, boardType, pageable);
    }
    
    // 제목과 내용으로 통합 검색
    public Page<Post> searchPosts(String keyword, BoardType boardType, Pageable pageable) {
        return postRepository.findByKeywordAndBoardType(keyword, boardType, pageable);
    }
    
    // 특정 사용자의 게시글 조회
    public Page<Post> getPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
    }
    
    // 인기 게시글 조회 (좋아요 수 기준)
    public Page<Post> getPopularPosts(BoardType boardType, Pageable pageable) {
        return postRepository.findPopularPosts(boardType, pageable);
    }
    
    // 조회수 기준 인기 게시글
    public Page<Post> getPopularPostsByViewCount(BoardType boardType, Pageable pageable) {
        return postRepository.findPopularPostsByViewCount(boardType, pageable);
    }
    
    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = getPostById(postId);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }
    
    // 좋아요 수 업데이트
    @Transactional
    public void updateLikeCount(Long postId, int delta) {
        Post post = getPostById(postId);
        post.setLikeCount(Math.max(0, post.getLikeCount() + delta));
        postRepository.save(post);
    }
    
    // 북마크 수 업데이트
    @Transactional
    public void updateBookmarkCount(Long postId, int delta) {
        Post post = getPostById(postId);
        post.setBookmarkCount(Math.max(0, post.getBookmarkCount() + delta));
        postRepository.save(post);
    }
    
    // 댓글 수 업데이트
    @Transactional
    public void updateCommentCount(Long postId, int delta) {
        Post post = getPostById(postId);
        post.setCommentCount(Math.max(0, post.getCommentCount() + delta));
        postRepository.save(post);
    }
}