package com.example.campus_house.controller;

import com.example.campus_house.entity.ResidenceVerification;
import com.example.campus_house.service.ResidenceVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class ResidenceVerificationController {
    
    private final ResidenceVerificationService verificationService;
    
    // 거주지 인증 신청
    @PostMapping("/request")
    public ResponseEntity<ResidenceVerification> requestVerification(@RequestBody VerificationRequest request) {
        try {
            ResidenceVerification verification = verificationService.requestVerification(
                    request.getUserId(),
                    request.getBuildingId(),
                    request.getBuildingName(),
                    request.getBuildingAddress(),
                    request.getRoomNumber(),
                    request.getVerificationDocument()
            );
            return ResponseEntity.ok(verification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 사용자의 인증 상태 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResidenceVerification> getUserVerification(@PathVariable Long userId) {
        Optional<ResidenceVerification> verification = verificationService.getUserVerification(userId);
        return verification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 사용자의 승인된 인증 조회
    @GetMapping("/user/{userId}/approved")
    public ResponseEntity<ResidenceVerification> getApprovedVerification(@PathVariable Long userId) {
        Optional<ResidenceVerification> verification = verificationService.getApprovedVerification(userId);
        return verification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 대기 중인 인증 신청 목록 조회 (관리자용)
    @GetMapping("/pending")
    public ResponseEntity<List<ResidenceVerification>> getPendingVerifications() {
        List<ResidenceVerification> verifications = verificationService.getPendingVerifications();
        return ResponseEntity.ok(verifications);
    }
    
    // 거주지 인증 승인 (관리자용)
    @PostMapping("/{verificationId}/approve")
    public ResponseEntity<ResidenceVerification> approveVerification(
            @PathVariable Long verificationId,
            @RequestBody AdminActionRequest request) {
        try {
            ResidenceVerification verification = verificationService.approveVerification(
                    verificationId,
                    request.getAdminId(),
                    request.getComment()
            );
            return ResponseEntity.ok(verification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 거주지 인증 거부 (관리자용)
    @PostMapping("/{verificationId}/reject")
    public ResponseEntity<ResidenceVerification> rejectVerification(
            @PathVariable Long verificationId,
            @RequestBody AdminActionRequest request) {
        try {
            ResidenceVerification verification = verificationService.rejectVerification(
                    verificationId,
                    request.getAdminId(),
                    request.getComment()
            );
            return ResponseEntity.ok(verification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 거주지 인증 취소
    @DeleteMapping("/{verificationId}")
    public ResponseEntity<Void> cancelVerification(
            @PathVariable Long verificationId,
            @RequestParam Long userId) {
        try {
            verificationService.cancelVerification(verificationId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 건물별 인증된 사용자 조회
    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<ResidenceVerification>> getVerifiedUsersByBuilding(@PathVariable Long buildingId) {
        List<ResidenceVerification> verifications = verificationService.getVerifiedUsersByBuilding(buildingId);
        return ResponseEntity.ok(verifications);
    }
    
    // DTO 클래스들
    public static class VerificationRequest {
        private Long userId;
        private Long buildingId;
        private String buildingName;
        private String buildingAddress;
        private String roomNumber;
        private String verificationDocument;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getBuildingId() { return buildingId; }
        public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
        public String getBuildingName() { return buildingName; }
        public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
        public String getBuildingAddress() { return buildingAddress; }
        public void setBuildingAddress(String buildingAddress) { this.buildingAddress = buildingAddress; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        public String getVerificationDocument() { return verificationDocument; }
        public void setVerificationDocument(String verificationDocument) { this.verificationDocument = verificationDocument; }
    }
    
    public static class AdminActionRequest {
        private Long adminId;
        private String comment;
        
        // Getters and Setters
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}
