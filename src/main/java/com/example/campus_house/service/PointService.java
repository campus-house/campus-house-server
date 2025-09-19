package com.example.campus_house.service;

import com.example.campus_house.entity.PointHistory;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.PointHistoryRepository;
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
public class PointService {
    
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    // 포인트 추가
    @Transactional
    public void addPoints(Long userId, PointHistory.PointType type, Integer amount, String description, String relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 포인트 추가
        user.setPoints(user.getPoints() + amount);
        userRepository.save(user);
        
        // 포인트 내역 기록
        PointHistory pointHistory = PointHistory.builder()
                .user(user)
                .type(type)
                .amount(amount)
                .balance(user.getPoints())
                .description(description)
                .relatedId(relatedId)
                .build();
        pointHistoryRepository.save(pointHistory);
        
        // 포인트 획득 알림 (양수인 경우만)
        if (amount > 0) {
            notificationService.createNotification(
                    userId,
                    com.example.campus_house.entity.Notification.NotificationType.POINT_EARNED,
                    "포인트를 획득했습니다!",
                    "+" + amount + " 포인트를 획득했습니다. (" + description + ")",
                    relatedId,
                    "POINT"
            );
        }
    }
    
    // 포인트 사용
    @Transactional
    public void usePoints(Long userId, PointHistory.PointType type, Integer amount, String description, String relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 포인트 확인
        if (user.getPoints() < amount) {
            throw new RuntimeException("포인트가 부족합니다. (보유: " + user.getPoints() + ", 필요: " + amount + ")");
        }
        
        // 포인트 차감
        user.setPoints(user.getPoints() - amount);
        userRepository.save(user);
        
        // 포인트 내역 기록
        PointHistory pointHistory = PointHistory.builder()
                .user(user)
                .type(type)
                .amount(-amount)
                .balance(user.getPoints())
                .description(description)
                .relatedId(relatedId)
                .build();
        pointHistoryRepository.save(pointHistory);
    }
    
    // 사용자별 포인트 내역 조회 (페이징)
    public Page<PointHistory> getPointHistory(Long userId, Pageable pageable) {
        return pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    // 사용자별 포인트 내역 조회 (전체)
    public List<PointHistory> getAllPointHistory(Long userId) {
        return pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // 사용자별 특정 타입의 포인트 내역 조회
    public List<PointHistory> getPointHistoryByType(Long userId, PointHistory.PointType type) {
        return pointHistoryRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }
    
    // 사용자별 기간별 포인트 내역 조회
    public List<PointHistory> getPointHistoryByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return pointHistoryRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startDate, endDate);
    }
    
    // 사용자별 총 포인트 획득량 조회
    public Integer getTotalEarnedPoints(Long userId) {
        return pointHistoryRepository.getTotalEarnedPointsByUserId(userId);
    }
    
    // 사용자별 총 포인트 사용량 조회
    public Integer getTotalUsedPoints(Long userId) {
        return pointHistoryRepository.getTotalUsedPointsByUserId(userId);
    }
    
    // 사용자별 특정 타입의 포인트 획득 횟수 조회
    public Long getEarnedPointsCountByType(Long userId, PointHistory.PointType type) {
        return pointHistoryRepository.countEarnedPointsByUserIdAndType(userId, type);
    }
    
    // 사용자 포인트 통계 조회
    public UserPointStats getUserPointStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Integer totalEarned = getTotalEarnedPoints(userId);
        Integer totalUsed = getTotalUsedPoints(userId);
        Long postCreateCount = getEarnedPointsCountByType(userId, PointHistory.PointType.POST_CREATE);
        Long commentCreateCount = getEarnedPointsCountByType(userId, PointHistory.PointType.COMMENT_CREATE);
        Long memoCreateCount = getEarnedPointsCountByType(userId, PointHistory.PointType.MEMO_CREATE);
        Long questionAnswerCount = getEarnedPointsCountByType(userId, PointHistory.PointType.QUESTION_ANSWER);
        
        return UserPointStats.builder()
                .currentPoints(user.getPoints())
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
    public static class UserPointStats {
        private Integer currentPoints;
        private Integer totalEarned;
        private Integer totalUsed;
        private Long postCreateCount;
        private Long commentCreateCount;
        private Long memoCreateCount;
        private Long questionAnswerCount;
    }
}
