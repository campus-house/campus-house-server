package com.example.campus_house.repository;

import com.example.campus_house.entity.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    
    /**
     * 특정 카테고리의 생활시설 조회 (페이징)
     */
    Page<Facility> findByCategory(String category, Pageable pageable);
    
    /**
     * 특정 카테고리의 생활시설 조회
     */
    List<Facility> findByCategoryAndBusinessStatus(String category, String businessStatus);
    
    /**
     * 특정 카테고리의 생활시설 개수 조회
     */
    long countByCategory(String category);
    
    /**
     * 영업중인 생활시설만 조회
     */
    List<Facility> findByBusinessStatus(String businessStatus);
    
    /**
     * 특정 카테고리의 영업중인 생활시설 조회
     */
    List<Facility> findByCategoryAndBusinessStatusOrderByBusinessName(String category, String businessStatus);
    
    /**
     * 주소로 생활시설 검색
     */
    List<Facility> findByAddressContaining(String address);
    
    /**
     * 사업장명으로 생활시설 검색
     */
    List<Facility> findByBusinessNameContaining(String businessName);
    
    /**
     * 반경 내 생활시설 조회 (하버사인 공식 사용)
     * @param latitude 중심 위도
     * @param longitude 중심 경도
     * @param radiusKm 반경 (km)
     * @return 반경 내 생활시설 목록
     */
    @Query(value = """
        SELECT * FROM facilities 
        WHERE business_status = '영업/정상'
        AND (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(latitude)) 
                * cos(radians(longitude) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(latitude))
            )
        ) <= :radiusKm
        """, nativeQuery = true)
    List<Facility> findNearbyFacilities(@Param("latitude") Double latitude, 
                                       @Param("longitude") Double longitude, 
                                       @Param("radiusKm") Double radiusKm);
    
    /**
     * 특정 카테고리의 반경 내 생활시설 조회
     */
    @Query(value = """
        SELECT * FROM facilities 
        WHERE business_status = '영업/정상'
        AND category = :category
        AND (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(latitude)) 
                * cos(radians(longitude) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(latitude))
            )
        ) <= :radiusKm
        """, nativeQuery = true)
    List<Facility> findNearbyFacilitiesByCategory(@Param("latitude") Double latitude, 
                                                 @Param("longitude") Double longitude, 
                                                 @Param("radiusKm") Double radiusKm,
                                                 @Param("category") String category);
    
    /**
     * 반경 내 생활시설 개수 조회
     */
    @Query(value = """
        SELECT COUNT(*) FROM facilities 
        WHERE business_status = '영업/정상'
        AND (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(latitude)) 
                * cos(radians(longitude) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(latitude))
            )
        ) <= :radiusKm
        """, nativeQuery = true)
    Long countNearbyFacilities(@Param("latitude") Double latitude, 
                              @Param("longitude") Double longitude, 
                              @Param("radiusKm") Double radiusKm);
    
    /**
     * 특정 카테고리의 반경 내 생활시설 개수 조회
     */
    @Query(value = """
        SELECT COUNT(*) FROM facilities 
        WHERE business_status = '영업/정상'
        AND category = :category
        AND (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(latitude)) 
                * cos(radians(longitude) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(latitude))
            )
        ) <= :radiusKm
        """, nativeQuery = true)
    Long countNearbyFacilitiesByCategory(@Param("latitude") Double latitude, 
                                        @Param("longitude") Double longitude, 
                                        @Param("radiusKm") Double radiusKm,
                                        @Param("category") String category);
}
