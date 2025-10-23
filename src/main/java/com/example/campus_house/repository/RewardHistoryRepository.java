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
    @Query("SELECT rh FROM RewardHistory rh WHERE rh.user.userId = :userId ORDER BY rh.createdAt DESC")
    Page<RewardHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 리워드 내역 조회 (전체)
    @Query("SELECT rh FROM RewardHistory rh WHERE rh.user.userId = :userId ORDER BY rh.createdAt DESC")
    List<RewardHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    // 사용자별 특정 타입의 리워드 내역 조회
    @Query("SELECT rh FROM RewardHistory rh WHERE rh.user.userId = :userId AND rh.type = :type ORDER BY rh.createdAt DESC")
    List<RewardHistory> findByUserIdAndTypeOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("type") RewardHistory.RewardType type);
    
    // 사용자별 기간별 리워드 내역 조회
    @Query("SELECT rh FROM RewardHistory rh WHERE rh.user.userId = :userId AND rh.createdAt BETWEEN :startDate AND :endDate ORDER BY rh.createdAt DESC")
    List<RewardHistory> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 사용자별 총 리워드 획득량 조회
    @Query("SELECT COALESCE(SUM(rh.amount), 0) FROM RewardHistory rh WHERE rh.user.userId = :userId AND rh.amount > 0")
    Integer getTotalEarnedRewardsByUserId(@Param("userId") Long userId);
    
    // 사용자별 총 리워드 사용량 조회
    @Query("SELECT COALESCE(SUM(ABS(rh.amount)), 0) FROM RewardHistory rh WHERE rh.user.userId = :userId AND rh.amount < 0")
    Integer getTotalUsedRewardsByUserId(@Param("userId") Long userId);
    
    // 사용자별 특정 타입의 리워드 획득 횟수 조회
    @Query("SELECT COUNT(rh) FROM RewardHistory rh WHERE rh.user.userId = :userId AND rh.type = :type AND rh.amount > 0")
    Long countEarnedRewardsByUserIdAndType(@Param("userId") Long userId, @Param("type") RewardHistory.RewardType type);
}
