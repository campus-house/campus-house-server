package com.example.campus_house.repository;

import com.example.campus_house.entity.MemoParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoParticipantRepository extends JpaRepository<MemoParticipant, Long> {
    
    // 특정 메모의 참여자 조회
    List<MemoParticipant> findByMemoIdOrderByCreatedAtAsc(Long memoId);
    
    // 특정 메모의 참여자 수 조회
    long countByMemoIdAndStatus(Long memoId, MemoParticipant.ParticipantStatus status);
    
    // 사용자가 특정 메모에 참여했는지 확인
    Optional<MemoParticipant> findByUserIdAndMemoId(Long userId, Long memoId);
    
    // 사용자가 특정 메모에 참여했는지 확인 (boolean)
    boolean existsByUserIdAndMemoId(Long userId, Long memoId);
    
    // 특정 사용자의 참여 목록 조회
    @Query("SELECT mp FROM MemoParticipant mp JOIN FETCH mp.memo WHERE mp.user.id = :userId ORDER BY mp.createdAt DESC")
    List<MemoParticipant> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
