package com.example.campus_house.repository;

import com.example.campus_house.entity.BuildingScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingScrapRepository extends JpaRepository<BuildingScrap, Long> {
    
    // 사용자별 건물 스크랩 목록 조회
    Page<BuildingScrap> findByUserId(Long userId, Pageable pageable);
    
    // 사용자별 건물 스크랩 목록 조회 (리스트)
    List<BuildingScrap> findByUserId(Long userId);
    
    // 특정 사용자가 특정 건물을 스크랩했는지 확인
    Optional<BuildingScrap> findByUserIdAndBuildingId(Long userId, Long buildingId);
    
    // 사용자가 스크랩한 건물 ID 목록
    List<Long> findBuildingIdsByUserId(Long userId);
    
    // 건물별 스크랩 수
    Long countByBuildingId(Long buildingId);
    
    // 사용자별 스크랩 수
    Long countByUserId(Long userId);
    
    // 스크랩 삭제
    void deleteByUserIdAndBuildingId(Long userId, Long buildingId);
}
