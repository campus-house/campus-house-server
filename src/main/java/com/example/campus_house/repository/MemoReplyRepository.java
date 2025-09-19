package com.example.campus_house.repository;

import com.example.campus_house.entity.MemoReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoReplyRepository extends JpaRepository<MemoReply, Long> {
    
    // 특정 메모의 답장/채팅 조회
    @Query("SELECT mr FROM MemoReply mr WHERE mr.memo.id = :memoId ORDER BY mr.createdAt ASC")
    List<MemoReply> findByMemoIdOrderByCreatedAtAsc(@Param("memoId") Long memoId);
    
    // 특정 사용자의 메모 답장 조회
    List<MemoReply> findByUserIdOrderByCreatedAtDesc(Long userId);
}
