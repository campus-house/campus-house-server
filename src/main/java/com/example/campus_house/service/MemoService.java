package com.example.campus_house.service;

import com.example.campus_house.entity.Memo;
import com.example.campus_house.entity.MemoReply;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.MemoReplyRepository;
import com.example.campus_house.repository.MemoRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {
    
    private final MemoRepository memoRepository;
    private final MemoReplyRepository memoReplyRepository;
    private final UserRepository userRepository;
    
    // 활성 메모 목록 조회
    public List<Memo> getActiveMemos() {
        return memoRepository.findActiveMemos(LocalDateTime.now());
    }
    
    // 메모 생성
    @Transactional
    public Memo createMemo(Long userId, String content, String imageUrl, Memo.MemoType type, 
                          String location, String contactInfo, LocalDateTime deadline) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Memo memo = Memo.builder()
                .user(user)
                .content(content)
                .imageUrl(imageUrl)
                .type(type)
                .location(location)
                .contactInfo(contactInfo)
                .deadline(deadline)
                .status(Memo.MemoStatus.ACTIVE)
                .build();
        
        return memoRepository.save(memo);
    }
    
    // 메모 답장/채팅 작성
    @Transactional
    public MemoReply createMemoReply(Long memoId, Long userId, String content, String imageUrl, MemoReply.ReplyType type) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new RuntimeException("메모를 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 메모가 만료되었는지 확인
        if (memo.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("만료된 메모입니다.");
        }
        
        MemoReply reply = MemoReply.builder()
                .memo(memo)
                .user(user)
                .content(content)
                .imageUrl(imageUrl)
                .type(type)
                .build();
        
        return memoReplyRepository.save(reply);
    }
    
    
    // 메모 답장/채팅 목록 조회
    public List<MemoReply> getMemoReplies(Long memoId) {
        return memoReplyRepository.findByMemoIdOrderByCreatedAtAsc(memoId);
    }
    
    
    // 만료된 메모 자동 처리 (스케줄러)
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @Transactional
    public void processExpiredMemos() {
        List<Memo> expiredMemos = memoRepository.findExpiredMemos(LocalDateTime.now());
        
        for (Memo memo : expiredMemos) {
            memo.setStatus(Memo.MemoStatus.EXPIRED);
            memoRepository.save(memo);
        }
    }
    
    // 특정 타입의 메모 조회
    public List<Memo> getMemosByType(Memo.MemoType type) {
        return memoRepository.findActiveMemosByType(type, LocalDateTime.now());
    }
    
    // 메모 삭제
    @Transactional
    public void deleteMemo(Long memoId, Long userId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new RuntimeException("메모를 찾을 수 없습니다."));
        
        // 작성자만 삭제 가능
        if (!memo.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("메모를 삭제할 권한이 없습니다.");
        }
        
        memo.setStatus(Memo.MemoStatus.EXPIRED);
        memoRepository.save(memo);
    }
}
