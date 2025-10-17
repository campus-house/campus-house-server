package com.example.campus_house.service;

import com.example.campus_house.entity.RewardHistory;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.RewardHistoryRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {
    
    private final RewardHistoryRepository rewardHistoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    // 리워드 추가
    @Transactional
    public void addRewards(Long userId, RewardHistory.RewardType type, Integer amount, String description, String relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 리워드 추가
        user.setRewards(user.getRewards() + amount);
        userRepository.save(user);
        
        // 리워드 내역 기록
        RewardHistory rewardHistory = RewardHistory.builder()
                .user(user)
                .type(type)
                .amount(amount)
                .balance(user.getRewards())
                .description(description)
                .relatedId(relatedId)
                .build();
        rewardHistoryRepository.save(rewardHistory);
        
        // 리워드 획득 알림 (양수인 경우만)
        if (amount > 0) {
            notificationService.createNotification(
                    userId,
                    com.example.campus_house.entity.Notification.NotificationType.POINT_EARNED,
                    "리워드를 획득했습니다!",
                    "+" + amount + " 리워드를 획득했습니다. (" + description + ")",
                    relatedId,
                    "REWARD"
            );
        }
    }
    
    // 리워드 사용
    @Transactional
    public void useRewards(Long userId, RewardHistory.RewardType type, Integer amount, String description, String relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 리워드 확인
        if (user.getRewards() < amount) {
            throw new RuntimeException("리워드가 부족합니다. (보유: " + user.getRewards() + ", 필요: " + amount + ")");
        }
        
        // 리워드 차감
        user.setRewards(user.getRewards() - amount);
        userRepository.save(user);
        
        // 리워드 내역 기록
        RewardHistory rewardHistory = RewardHistory.builder()
                .user(user)
                .type(type)
                .amount(-amount)
                .balance(user.getRewards())
                .description(description)
                .relatedId(relatedId)
                .build();
        rewardHistoryRepository.save(rewardHistory);
    }
    
    // 사용자별 리워드 내역 조회 (페이징)
    public Page<RewardHistory> getRewardHistory(Long userId, Pageable pageable) {
        return rewardHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    // 사용자별 리워드 내역 조회 (전체)
    public List<RewardHistory> getAllRewardHistory(Long userId) {
        return rewardHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // 사용자별 특정 타입의 리워드 내역 조회
    public List<RewardHistory> getRewardHistoryByType(Long userId, RewardHistory.RewardType type) {
        return rewardHistoryRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }
    
    // 사용자별 기간별 리워드 내역 조회
    public List<RewardHistory> getRewardHistoryByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return rewardHistoryRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startDate, endDate);
    }
    
    // 사용자별 총 리워드 획득량 조회
    public Integer getTotalEarnedRewards(Long userId) {
        return rewardHistoryRepository.getTotalEarnedRewardsByUserId(userId);
    }
    
    // 사용자별 총 리워드 사용량 조회
    public Integer getTotalUsedRewards(Long userId) {
        return rewardHistoryRepository.getTotalUsedRewardsByUserId(userId);
    }
    
    // 사용자별 특정 타입의 리워드 획득 횟수 조회
    public Long getEarnedRewardsCountByType(Long userId, RewardHistory.RewardType type) {
        return rewardHistoryRepository.countEarnedRewardsByUserIdAndType(userId, type);
    }
    
    // 사용자 리워드 통계 조회
    public UserRewardStats getUserRewardStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Integer totalEarned = getTotalEarnedRewards(userId);
        Integer totalUsed = getTotalUsedRewards(userId);
        Long postCreateCount = getEarnedRewardsCountByType(userId, RewardHistory.RewardType.POST_CREATE);
        Long commentCreateCount = getEarnedRewardsCountByType(userId, RewardHistory.RewardType.COMMENT_CREATE);
        Long memoCreateCount = getEarnedRewardsCountByType(userId, RewardHistory.RewardType.MEMO_CREATE);
        Long questionAnswerCount = getEarnedRewardsCountByType(userId, RewardHistory.RewardType.QUESTION_ANSWER);
        
        return UserRewardStats.builder()
                .currentRewards(user.getRewards())
                .totalEarned(totalEarned)
                .totalUsed(totalUsed)
                .postCreateCount(postCreateCount)
                .commentCreateCount(commentCreateCount)
                .memoCreateCount(memoCreateCount)
                .questionAnswerCount(questionAnswerCount)
                .build();
    }
    
    // DTO 클래스
    @lombok.Data
    @lombok.Builder
    public static class UserRewardStats {
        private Integer currentRewards;
        private Integer totalEarned;
        private Integer totalUsed;
        private Long postCreateCount;
        private Long commentCreateCount;
        private Long memoCreateCount;
        private Long questionAnswerCount;
    }
}
