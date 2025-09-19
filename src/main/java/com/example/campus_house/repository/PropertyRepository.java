package com.example.campus_house.repository;

import com.example.campus_house.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    // 위치 기반 매물 검색 (반경 내)
    @Query("SELECT p FROM Property p WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * " +
           "cos(radians(p.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(p.latitude))) <= :radius")
    List<Property> findPropertiesWithinRadius(@Param("lat") double latitude, 
                                            @Param("lng") double longitude, 
                                            @Param("radius") double radiusKm);
    
    // 건물명으로 검색
    Page<Property> findByBuildingNameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(
            String buildingName, Property.PropertyStatus status, Pageable pageable);
    
    // 주소로 검색
    Page<Property> findByAddressContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(
            String address, Property.PropertyStatus status, Pageable pageable);
    
    // 매물 타입별 검색
    Page<Property> findByPropertyTypeAndStatusOrderByCreatedAtDesc(
            Property.PropertyType propertyType, Property.PropertyStatus status, Pageable pageable);
    
    // 층수 타입별 검색
    Page<Property> findByFloorTypeAndStatusOrderByCreatedAtDesc(
            Property.FloorType floorType, Property.PropertyStatus status, Pageable pageable);
    
    // 가격 범위 검색
    @Query("SELECT p FROM Property p WHERE p.deposit BETWEEN :minDeposit AND :maxDeposit " +
           "AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Property> findByDepositRange(@Param("minDeposit") BigDecimal minDeposit,
                                    @Param("maxDeposit") BigDecimal maxDeposit,
                                    @Param("status") Property.PropertyStatus status,
                                    Pageable pageable);
    
    // 월세 범위 검색
    @Query("SELECT p FROM Property p WHERE p.monthlyRent BETWEEN :minRent AND :maxRent " +
           "AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Property> findByMonthlyRentRange(@Param("minRent") BigDecimal minRent,
                                        @Param("maxRent") BigDecimal maxRent,
                                        @Param("status") Property.PropertyStatus status,
                                        Pageable pageable);
    
    // 복합 검색 (타입, 층수, 가격)
    @Query("SELECT p FROM Property p WHERE " +
           "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
           "(:floorType IS NULL OR p.floorType = :floorType) AND " +
           "(:minDeposit IS NULL OR p.deposit >= :minDeposit) AND " +
           "(:maxDeposit IS NULL OR p.deposit <= :maxDeposit) AND " +
           "(:minRent IS NULL OR p.monthlyRent >= :minRent) AND " +
           "(:maxRent IS NULL OR p.monthlyRent <= :maxRent) AND " +
           "p.status = :status ORDER BY p.createdAt DESC")
    Page<Property> findPropertiesWithFilters(@Param("propertyType") Property.PropertyType propertyType,
                                           @Param("floorType") Property.FloorType floorType,
                                           @Param("minDeposit") BigDecimal minDeposit,
                                           @Param("maxDeposit") BigDecimal maxDeposit,
                                           @Param("minRent") BigDecimal minRent,
                                           @Param("maxRent") BigDecimal maxRent,
                                           @Param("status") Property.PropertyStatus status,
                                           Pageable pageable);
    
    // 키워드 검색 (건물명 + 주소)
    @Query("SELECT p FROM Property p WHERE " +
           "(p.buildingName LIKE %:keyword% OR p.address LIKE %:keyword%) AND " +
           "p.status = :status ORDER BY p.createdAt DESC")
    Page<Property> findByKeyword(@Param("keyword") String keyword,
                                @Param("status") Property.PropertyStatus status,
                                Pageable pageable);
    
    // 인기 매물 (스크랩 수 기준)
    @Query("SELECT p FROM Property p WHERE p.status = :status ORDER BY p.scrapCount DESC, p.createdAt DESC")
    Page<Property> findPopularProperties(@Param("status") Property.PropertyStatus status, Pageable pageable);
    
    // 최근 등록된 매물
    Page<Property> findByStatusOrderByCreatedAtDesc(Property.PropertyStatus status, Pageable pageable);
    
    // 특정 사용자의 스크랩한 매물
    @Query("SELECT p FROM Property p JOIN p.scraps s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    Page<Property> findScrapedPropertiesByUserId(@Param("userId") Long userId, Pageable pageable);
}
