package com.example.campus_house.repository;

import com.example.campus_house.entity.PropertyTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
// import java.util.List; // 현재 사용하지 않음

@Repository
public interface PropertyTransferRepository extends JpaRepository<PropertyTransfer, Long> {
    
    // 특정 매물의 양도 정보 조회
    Page<PropertyTransfer> findByPropertyIdAndStatusOrderByCreatedAtDesc(
            Long propertyId, PropertyTransfer.TransferStatus status, Pageable pageable);
    
    // 특정 사용자의 양도 정보 조회
    Page<PropertyTransfer> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, PropertyTransfer.TransferStatus status, Pageable pageable);
    
    // 양도 가능한 정보 조회
    Page<PropertyTransfer> findByStatusOrderByCreatedAtDesc(
            PropertyTransfer.TransferStatus status, Pageable pageable);
    
    // 양도 타입별 조회
    Page<PropertyTransfer> findByTypeAndStatusOrderByCreatedAtDesc(
            PropertyTransfer.TransferType type, PropertyTransfer.TransferStatus status, Pageable pageable);
    
    // 입주 가능일 기준 조회
    @Query("SELECT t FROM PropertyTransfer t WHERE t.moveInDate >= :startDate AND t.moveInDate <= :endDate AND t.status = 'AVAILABLE' ORDER BY t.moveInDate ASC")
    Page<PropertyTransfer> findByMoveInDateRange(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               Pageable pageable);
    
    // 최근 양도 정보
    @Query("SELECT t FROM PropertyTransfer t WHERE t.status = 'AVAILABLE' ORDER BY t.createdAt DESC")
    Page<PropertyTransfer> findRecentTransfers(Pageable pageable);
    
    // 인기 양도 정보 (조회수 기준)
    @Query("SELECT t FROM PropertyTransfer t WHERE t.status = 'AVAILABLE' ORDER BY t.viewCount DESC, t.createdAt DESC")
    Page<PropertyTransfer> findPopularTransfers(Pageable pageable);
}
