package com.example.campus_house.repository;

import com.example.campus_house.entity.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    // 키워드로 건물 검색
    @Query("SELECT b FROM Building b WHERE " +
           "b.buildingName LIKE %:keyword% OR " +
           "b.address LIKE %:keyword%")
    Page<Building> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    
    // 위치 기반 검색 (반경 내)
    @Query("SELECT b FROM Building b WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(b.latitude)) * " +
           "cos(radians(b.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(b.latitude))) <= :radius")
    Page<Building> findByLocationWithinRadius(@Param("lat") Double latitude, 
                                            @Param("lng") Double longitude, 
                                            @Param("radius") Double radiusKm, 
                                            Pageable pageable);
    
    // 보증금 범위 검색
    Page<Building> findByDepositBetween(BigDecimal minDeposit, BigDecimal maxDeposit, Pageable pageable);
    
    // 월세 범위 검색
    Page<Building> findByMonthlyRentBetween(BigDecimal minMonthlyRent, BigDecimal maxMonthlyRent, Pageable pageable);
    
    // 전세 범위 검색
    Page<Building> findByJeonseBetween(BigDecimal minJeonse, BigDecimal maxJeonse, Pageable pageable);
    
    
    // 엘리베이터 필터 (승강기대수가 0보다 큰 경우)
    Page<Building> findByElevatorsGreaterThan(Integer minElevators, Pageable pageable);
    
    // 학교 접근성 필터 (걸리는 시간 기준)
    Page<Building> findBySchoolWalkingTimeLessThanEqual(Integer maxWalkingTime, Pageable pageable);
    
    // 복합 필터링
    @Query("SELECT b FROM Building b WHERE " +
           "(:minDeposit IS NULL OR b.deposit >= :minDeposit) AND " +
           "(:maxDeposit IS NULL OR b.deposit <= :maxDeposit) AND " +
           "(:minMonthlyRent IS NULL OR b.monthlyRent >= :minMonthlyRent) AND " +
           "(:maxMonthlyRent IS NULL OR b.monthlyRent <= :maxMonthlyRent) AND " +
           "(:minJeonse IS NULL OR b.jeonse >= :minJeonse) AND " +
           "(:maxJeonse IS NULL OR b.jeonse <= :maxJeonse) AND " +
           "(:elevatorRequired IS NULL OR (:elevatorRequired = true AND b.elevators > 0)) AND " +
           "(:maxWalkingTime IS NULL OR b.schoolWalkingTime <= :maxWalkingTime) AND " +
           "(:buildingUsage IS NULL OR b.buildingUsage LIKE %:buildingUsage%)")
    Page<Building> findByFilters(@Param("minDeposit") BigDecimal minDeposit,
                                @Param("maxDeposit") BigDecimal maxDeposit,
                                @Param("minMonthlyRent") BigDecimal minMonthlyRent,
                                @Param("maxMonthlyRent") BigDecimal maxMonthlyRent,
                                @Param("minJeonse") BigDecimal minJeonse,
                                @Param("maxJeonse") BigDecimal maxJeonse,
                                @Param("elevatorRequired") Boolean elevatorRequired,
                                @Param("maxWalkingTime") Integer maxWalkingTime,
                                @Param("buildingUsage") String buildingUsage,
                                Pageable pageable);
    
    
    
    // 건물 용도별 검색
    Page<Building> findByBuildingUsageContaining(String buildingUsage, Pageable pageable);
    
    // 건물 용도 목록 조회
    @Query("SELECT DISTINCT b.buildingUsage FROM Building b WHERE b.buildingUsage IS NOT NULL ORDER BY b.buildingUsage")
    java.util.List<String> findDistinctBuildingUsages();
    
    // 편의점 개수로 검색 (외부 API 연동 예정)
    Page<Building> findByNearbyConvenienceStoresGreaterThanEqual(Integer minConvenienceStores, Pageable pageable);
    
    // 마트 개수로 검색 (외부 API 연동 예정)
    Page<Building> findByNearbyMartsGreaterThanEqual(Integer minMarts, Pageable pageable);
    
    // 병원 개수로 검색 (외부 API 연동 예정)
    Page<Building> findByNearbyHospitalsGreaterThanEqual(Integer minHospitals, Pageable pageable);
    
    // 특정 건물명이 아닌 건물들 삭제 (샘플 데이터 제외용)
    int deleteByBuildingNameNotIn(java.util.List<String> buildingNames);
    
    // 좌표가 없는 건물들 조회
    java.util.List<Building> findByLatitudeIsNullOrLongitudeIsNull();
    
    // 좌표가 있는 건물 수 조회
    long countByLatitudeIsNotNullAndLongitudeIsNotNull();
}
