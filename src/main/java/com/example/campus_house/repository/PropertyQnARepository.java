package com.example.campus_house.repository;

import com.example.campus_house.entity.PropertyQnA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyQnARepository extends JpaRepository<PropertyQnA, Long> {
    
    // 특정 매물의 Q&A 조회 (질문만)
    @Query("SELECT q FROM PropertyQnA q WHERE q.property.id = :propertyId AND q.type = 'QUESTION' AND q.status = 'ACTIVE' ORDER BY q.createdAt DESC")
    Page<PropertyQnA> findQuestionsByPropertyId(@Param("propertyId") Long propertyId, Pageable pageable);
    
    // 특정 매물의 Q&A 조회 (전체)
    Page<PropertyQnA> findByPropertyIdAndStatusOrderByCreatedAtDesc(
            Long propertyId, PropertyQnA.QnAStatus status, Pageable pageable);
    
    // 특정 질문의 답변 조회
    @Query("SELECT q FROM PropertyQnA q WHERE q.parent.id = :parentId AND q.status = 'ACTIVE' ORDER BY q.createdAt ASC")
    List<PropertyQnA> findAnswersByParentId(@Param("parentId") Long parentId);
    
    // 특정 매물의 Q&A 수 조회
    long countByPropertyIdAndStatus(Long propertyId, PropertyQnA.QnAStatus status);
    
    // 특정 사용자의 Q&A 조회
    Page<PropertyQnA> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, PropertyQnA.QnAStatus status, Pageable pageable);
    
    // 답변이 없는 질문 조회
    @Query("SELECT q FROM PropertyQnA q WHERE q.property.id = :propertyId AND q.type = 'QUESTION' AND q.status = 'ACTIVE' AND q.id NOT IN (SELECT a.parent.id FROM PropertyQnA a WHERE a.parent IS NOT NULL AND a.status = 'ACTIVE') ORDER BY q.createdAt DESC")
    Page<PropertyQnA> findUnansweredQuestionsByPropertyId(@Param("propertyId") Long propertyId, Pageable pageable);
    
    // 인기 Q&A (좋아요 수 기준)
    @Query("SELECT q FROM PropertyQnA q WHERE q.status = 'ACTIVE' ORDER BY q.likeCount DESC, q.createdAt DESC")
    Page<PropertyQnA> findPopularQnAs(Pageable pageable);
}
