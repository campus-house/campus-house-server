package com.example.campus_house.repository;

import com.example.campus_house.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    
    // 활성 메모 조회 (24시간 이내)
    @Query("SELECT m FROM Memo m WHERE m.status = 'ACTIVE' AND m.expiresAt > :now ORDER BY m.createdAt DESC")
    List<Memo> findActiveMemos(@Param("now") LocalDateTime now);
    
    // 만료된 메모 조회
    @Query("SELECT m FROM Memo m WHERE m.status = 'ACTIVE' AND m.expiresAt <= :now")
    List<Memo> findExpiredMemos(@Param("now") LocalDateTime now);
    
    // 특정 사용자의 메모 조회
    List<Memo> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Memo.MemoStatus status);
    
    // 특정 타입의 활성 메모 조회
    @Query("SELECT m FROM Memo m WHERE m.type = :type AND m.status = 'ACTIVE' AND m.expiresAt > :now ORDER BY m.createdAt DESC")
    List<Memo> findActiveMemosByType(@Param("type") Memo.MemoType type, @Param("now") LocalDateTime now);
}
