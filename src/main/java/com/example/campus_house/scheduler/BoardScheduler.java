package com.example.campus_house.scheduler;

import com.example.campus_house.entity.Post;
import com.example.campus_house.repository.PostRepository;
import com.example.campus_house.service.MemoService;
import com.example.campus_house.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BoardScheduler {
    
    private final MemoService memoService;
    private final PostService postService;
    private final PostRepository postRepository;
    
    // 1시간마다 실행 - 만료된 메모 처리
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void processExpiredMemos() {
        memoService.processExpiredMemos();
    }
    
    // 24시간마다 실행 - 새 질문 표시 해제
    @Scheduled(fixedRate = 86400000)
    @Transactional
    public void markQuestionsAsNotNew() {
        // 24시간 이전에 생성된 새 질문들을 찾아서 표시 해제
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        
        List<Post> oldNewQuestions = postRepository.findByBoardCategoryAndBoardTypeAndStatusAndIsNewAndCreatedAtBefore(
                Post.BoardCategory.APARTMENT,
                Post.BoardType.QUESTION, 
                Post.PostStatus.ACTIVE, 
                true, 
                twentyFourHoursAgo
        );
        
        for (Post post : oldNewQuestions) {
            postService.markQuestionAsNotNew(post.getId());
        }
    }
}
