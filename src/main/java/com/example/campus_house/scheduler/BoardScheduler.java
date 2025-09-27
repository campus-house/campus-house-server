package com.example.campus_house.scheduler;

import com.example.campus_house.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BoardScheduler {
    
    private final MemoService memoService;
    
    // 1시간마다 실행 - 만료된 메모 처리
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void processExpiredMemos() {
        memoService.processExpiredMemos();
    }
}