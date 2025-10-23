package com.example.campus_house.repository;

import com.example.campus_house.entity.BuildingScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingScrapRepository extends JpaRepository<BuildingScrap, Long> {
    
    // 사용자별 건물 스크랩 목록 조회
    @Query("SELECT bs FROM BuildingScrap bs WHERE bs.user.userId = :userId")
    Page<BuildingScrap> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 건물 스크랩 목록 조회 (리스트)
    @Query("SELECT bs FROM BuildingScrap bs WHERE bs.user.userId = :userId")
    List<BuildingScrap> findByUserId(@Param("userId") Long userId);
    
    // 특정 사용자가 특정 건물을 스크랩했는지 확인
    @Query("SELECT bs FROM BuildingScrap bs WHERE bs.user.userId = :userId AND bs.building.id = :buildingId")
    Optional<BuildingScrap> findByUserIdAndBuildingId(@Param("userId") Long userId, @Param("buildingId") Long buildingId);
    
    // 사용자가 스크랩한 건물 ID 목록
    @Query("SELECT bs.building.id FROM BuildingScrap bs WHERE bs.user.userId = :userId")
    List<Long> findBuildingIdsByUserId(@Param("userId") Long userId);
    
    // 건물별 스크랩 수
    Long countByBuildingId(Long buildingId);
    
    // 사용자별 스크랩 수
    @Query("SELECT COUNT(bs) FROM BuildingScrap bs WHERE bs.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    // 스크랩 삭제
    @Query("DELETE FROM BuildingScrap bs WHERE bs.user.userId = :userId AND bs.building.id = :buildingId")
    void deleteByUserIdAndBuildingId(@Param("userId") Long userId, @Param("buildingId") Long buildingId);
}
