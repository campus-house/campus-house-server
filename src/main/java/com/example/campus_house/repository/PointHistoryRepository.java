package com.example.campus_house.repository;

import com.example.campus_house.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    
    // 사용자별 포인트 내역 조회 (페이징)
    Page<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 사용자별 포인트 내역 조회 (전체)
    List<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 사용자별 특정 타입의 포인트 내역 조회
    List<PointHistory> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, PointHistory.PointType type);
    
    // 사용자별 기간별 포인트 내역 조회
    List<PointHistory> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // 사용자별 총 포인트 획득량 조회
    @Query("SELECT COALESCE(SUM(ph.amount), 0) FROM PointHistory ph WHERE ph.user.id = :userId AND ph.amount > 0")
    Integer getTotalEarnedPointsByUserId(@Param("userId") Long userId);
    
    // 사용자별 총 포인트 사용량 조회
    @Query("SELECT COALESCE(SUM(ABS(ph.amount)), 0) FROM PointHistory ph WHERE ph.user.id = :userId AND ph.amount < 0")
    Integer getTotalUsedPointsByUserId(@Param("userId") Long userId);
    
    // 사용자별 특정 타입의 포인트 획득 횟수 조회
    @Query("SELECT COUNT(ph) FROM PointHistory ph WHERE ph.user.id = :userId AND ph.type = :type AND ph.amount > 0")
    Long countEarnedPointsByUserIdAndType(@Param("userId") Long userId, @Param("type") PointHistory.PointType type);
}
