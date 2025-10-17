package com.example.campus_house.service;

import com.example.campus_house.entity.ResidenceVerification;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.ResidenceVerificationRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResidenceVerificationService {
    
    private final ResidenceVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    
    // 거주지 인증 신청
    @Transactional
    public ResidenceVerification requestVerification(Long userId, Long buildingId, String buildingName, 
                                                   String buildingAddress, String roomNumber, String verificationDocument) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 이미 승인된 인증이 있는지 확인
        Optional<ResidenceVerification> existingApproved = verificationRepository.findByUserIdAndStatus(
                userId, ResidenceVerification.VerificationStatus.APPROVED);
        if (existingApproved.isPresent()) {
            throw new RuntimeException("이미 거주지 인증이 완료된 사용자입니다.");
        }
        
        // 대기 중인 인증이 있는지 확인
        Optional<ResidenceVerification> existingPending = verificationRepository.findByUserIdAndStatus(
                userId, ResidenceVerification.VerificationStatus.PENDING);
        if (existingPending.isPresent()) {
            throw new RuntimeException("이미 거주지 인증이 대기 중입니다.");
        }
        
        ResidenceVerification verification = ResidenceVerification.builder()
                .user(user)
                .buildingId(buildingId)
                .buildingName(buildingName)
                .buildingAddress(buildingAddress)
                .roomNumber(roomNumber)
                .status(ResidenceVerification.VerificationStatus.PENDING)
                .build();
        
        return verificationRepository.save(verification);
    }
    
    // 거주지 인증 승인
    @Transactional
    public ResidenceVerification approveVerification(Long verificationId, Long adminId, String adminComment) {
        ResidenceVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("인증 신청을 찾을 수 없습니다."));
        
        if (verification.getStatus() != ResidenceVerification.VerificationStatus.PENDING) {
            throw new RuntimeException("대기 중인 인증 신청만 승인할 수 있습니다.");
        }
        
        // 인증 상태 업데이트
        verification.setStatus(ResidenceVerification.VerificationStatus.APPROVED);
        verification.setVerifiedBy(adminId);
        verification.setVerifiedAt(LocalDateTime.now());
        
        // 사용자 정보 업데이트
        User user = verification.getUser();
        user.setUserType(User.UserType.RESIDENT);
        user.setIsVerified(true);
        user.setVerifiedBuildingId(verification.getBuildingId());
        user.setVerifiedBuildingName(verification.getBuildingName());
        user.setVerifiedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        return verificationRepository.save(verification);
    }
    
    // 거주지 인증 거부
    @Transactional
    public ResidenceVerification rejectVerification(Long verificationId, Long adminId, String adminComment) {
        ResidenceVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("인증 신청을 찾을 수 없습니다."));
        
        if (verification.getStatus() != ResidenceVerification.VerificationStatus.PENDING) {
            throw new RuntimeException("대기 중인 인증 신청만 거부할 수 있습니다.");
        }
        
        verification.setStatus(ResidenceVerification.VerificationStatus.REJECTED);
        verification.setVerifiedBy(adminId);
        verification.setVerifiedAt(LocalDateTime.now());
        
        return verificationRepository.save(verification);
    }
    
    // 사용자의 인증 상태 조회
    public Optional<ResidenceVerification> getUserVerification(Long userId) {
        return verificationRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // 사용자의 승인된 인증 조회
    public Optional<ResidenceVerification> getApprovedVerification(Long userId) {
        return verificationRepository.findByUserIdAndStatus(userId, ResidenceVerification.VerificationStatus.APPROVED);
    }
    
    // 대기 중인 인증 신청 목록 조회
    public List<ResidenceVerification> getPendingVerifications() {
        return verificationRepository.findByStatusOrderByCreatedAtDesc(ResidenceVerification.VerificationStatus.PENDING);
    }
    
    // 건물별 인증된 사용자 조회
    public List<ResidenceVerification> getVerifiedUsersByBuilding(Long buildingId) {
        return verificationRepository.findByBuildingIdAndStatus(buildingId, ResidenceVerification.VerificationStatus.APPROVED);
    }
    
    // 거주지 인증 취소 (사용자가 직접 취소)
    @Transactional
    public void cancelVerification(Long verificationId, Long userId) {
        ResidenceVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("인증 신청을 찾을 수 없습니다."));
        
        if (!verification.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 인증 신청만 취소할 수 있습니다.");
        }
        
        if (verification.getStatus() != ResidenceVerification.VerificationStatus.PENDING) {
            throw new RuntimeException("대기 중인 인증 신청만 취소할 수 있습니다.");
        }
        
        verificationRepository.delete(verification);
    }
}
