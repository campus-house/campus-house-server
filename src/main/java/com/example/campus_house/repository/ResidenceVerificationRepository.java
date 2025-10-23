package com.example.campus_house.repository;

import com.example.campus_house.entity.ResidenceVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidenceVerificationRepository extends JpaRepository<ResidenceVerification, Long> {
    
    // 사용자별 인증 신청 조회
    @Query("SELECT rv FROM ResidenceVerification rv WHERE rv.user.userId = :userId ORDER BY rv.createdAt DESC")
    List<ResidenceVerification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    // 사용자의 최신 인증 신청 조회
    @Query("SELECT rv FROM ResidenceVerification rv WHERE rv.user.userId = :userId ORDER BY rv.createdAt DESC")
    Optional<ResidenceVerification> findFirstByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    // 상태별 인증 신청 조회
    List<ResidenceVerification> findByStatusOrderByCreatedAtDesc(ResidenceVerification.VerificationStatus status);
    
    // 건물별 인증된 사용자 조회
    List<ResidenceVerification> findByBuildingIdAndStatus(Long buildingId, ResidenceVerification.VerificationStatus status);
    
    // 사용자의 승인된 인증 조회
    @Query("SELECT rv FROM ResidenceVerification rv WHERE rv.user.userId = :userId AND rv.status = :status")
    Optional<ResidenceVerification> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ResidenceVerification.VerificationStatus status);
}
