package com.example.campus_house.repository;

import com.example.campus_house.entity.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    // 키워드로 건물 검색
    @Query("SELECT b FROM Building b WHERE " +
           "b.buildingName LIKE %:keyword% OR " +
           "b.address LIKE %:keyword% OR " +
           "b.nearbyFacilities LIKE %:keyword%")
    Page<Building> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 건물명으로 검색
    Page<Building> findByBuildingNameContaining(String buildingName, Pageable pageable);
    
    // 주소로 검색
    Page<Building> findByAddressContaining(String address, Pageable pageable);
    
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
    
    // 주차장 필터 (주차대수가 0보다 큰 경우)
    Page<Building> findByParkingSpacesGreaterThan(Integer minParkingSpaces, Pageable pageable);
    
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
           "(:parkingRequired IS NULL OR (:parkingRequired = true AND b.parkingSpaces > 0)) AND " +
           "(:elevatorRequired IS NULL OR (:elevatorRequired = true AND b.elevators > 0)) AND " +
           "(:maxWalkingTime IS NULL OR b.schoolWalkingTime <= :maxWalkingTime)")
    Page<Building> findByFilters(@Param("minDeposit") BigDecimal minDeposit,
                                @Param("maxDeposit") BigDecimal maxDeposit,
                                @Param("minMonthlyRent") BigDecimal minMonthlyRent,
                                @Param("maxMonthlyRent") BigDecimal maxMonthlyRent,
                                @Param("minJeonse") BigDecimal minJeonse,
                                @Param("maxJeonse") BigDecimal maxJeonse,
                                @Param("parkingRequired") Boolean parkingRequired,
                                @Param("elevatorRequired") Boolean elevatorRequired,
                                @Param("maxWalkingTime") Integer maxWalkingTime,
                                Pageable pageable);
    
    // 최근 등록된 건물 조회
    Page<Building> findByOrderByCreatedAtDesc(Pageable pageable);
    
    // 인기 건물 조회 (스크랩 수 기준)
    Page<Building> findByOrderByScrapCountDesc(Pageable pageable);
    
    // 영통역 접근성 필터
    Page<Building> findByStationWalkingTimeLessThanEqual(Integer maxWalkingTime, Pageable pageable);
}
