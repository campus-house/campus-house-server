package com.example.campus_house.repository;

import com.example.campus_house.entity.PropertyScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyScrapRepository extends JpaRepository<PropertyScrap, Long> {
    
    // 사용자가 특정 매물을 스크랩했는지 확인
    Optional<PropertyScrap> findByUserIdAndPropertyId(Long userId, Long propertyId);
    
    // 사용자가 특정 매물을 스크랩했는지 확인 (boolean)
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
    
    // 사용자의 스크랩한 매물 조회
    @Query("SELECT s FROM PropertyScrap s JOIN FETCH s.property WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    Page<PropertyScrap> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자의 스크랩한 매물 조회 (페이징 없음)
    @Query("SELECT s FROM PropertyScrap s JOIN FETCH s.property WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<PropertyScrap> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    // 특정 매물의 스크랩 수 조회
    long countByPropertyId(Long propertyId);
    
    // 특정 매물의 스크랩 삭제
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
}
