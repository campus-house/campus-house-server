package com.example.campus_house.repository;

import com.example.campus_house.entity.RewardHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardHistoryRepository extends JpaRepository<RewardHistory, Long> {
    
    // 사용자별 리워드 내역 조회 (페이징)
    Page<RewardHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 사용자별 리워드 내역 조회 (전체)
    List<RewardHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 사용자별 특정 타입의 리워드 내역 조회
    List<RewardHistory> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, RewardHistory.RewardType type);
    
    // 사용자별 기간별 리워드 내역 조회
    List<RewardHistory> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // 사용자별 총 리워드 획득량 조회
    @Query("SELECT COALESCE(SUM(rh.amount), 0) FROM RewardHistory rh WHERE rh.user.id = :userId AND rh.amount > 0")
    Integer getTotalEarnedRewardsByUserId(@Param("userId") Long userId);
    
    // 사용자별 총 리워드 사용량 조회
    @Query("SELECT COALESCE(SUM(ABS(rh.amount)), 0) FROM RewardHistory rh WHERE rh.user.id = :userId AND rh.amount < 0")
    Integer getTotalUsedRewardsByUserId(@Param("userId") Long userId);
    
    // 사용자별 특정 타입의 리워드 획득 횟수 조회
    @Query("SELECT COUNT(rh) FROM RewardHistory rh WHERE rh.user.id = :userId AND rh.type = :type AND rh.amount > 0")
    Long countEarnedRewardsByUserIdAndType(@Param("userId") Long userId, @Param("type") RewardHistory.RewardType type);
}
