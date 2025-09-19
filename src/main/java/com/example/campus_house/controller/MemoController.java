package com.example.campus_house.controller;

import com.example.campus_house.entity.Memo;
import com.example.campus_house.entity.MemoParticipant;
import com.example.campus_house.entity.MemoReply;
import com.example.campus_house.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {
    
    private final MemoService memoService;
    
    // 활성 메모 목록 조회
    @GetMapping
    public ResponseEntity<List<Memo>> getActiveMemos() {
        List<Memo> memos = memoService.getActiveMemos();
        return ResponseEntity.ok(memos);
    }
    
    // 특정 타입의 메모 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Memo>> getMemosByType(@PathVariable String type) {
        try {
            Memo.MemoType memoType = Memo.MemoType.valueOf(type.toUpperCase());
            List<Memo> memos = memoService.getMemosByType(memoType);
            return ResponseEntity.ok(memos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 메모 생성
    @PostMapping
    public ResponseEntity<Memo> createMemo(@RequestBody CreateMemoRequest request) {
        try {
            Memo memo = memoService.createMemo(
                    request.getUserId(),
                    request.getContent(),
                    request.getImageUrl(),
                    request.getType(),
                    request.getLocation(),
                    request.getMaxParticipants(),
                    request.getContactInfo(),
                    request.getDeadline()
            );
            return ResponseEntity.ok(memo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 메모 답장/채팅 작성
    @PostMapping("/{memoId}/replies")
    public ResponseEntity<MemoReply> createMemoReply(
            @PathVariable Long memoId,
            @RequestBody CreateMemoReplyRequest request) {
        try {
            MemoReply reply = memoService.createMemoReply(
                    memoId,
                    request.getUserId(),
                    request.getContent(),
                    request.getImageUrl(),
                    request.getType()
            );
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 메모 참여 신청
    @PostMapping("/{memoId}/participate")
    public ResponseEntity<MemoParticipant> participateInMemo(
            @PathVariable Long memoId,
            @RequestBody ParticipateRequest request) {
        try {
            MemoParticipant participant = memoService.participateInMemo(
                    memoId,
                    request.getUserId(),
                    request.getMessage()
            );
            return ResponseEntity.ok(participant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 메모 참여 승인/거부
    @PutMapping("/participants/{participantId}")
    public ResponseEntity<MemoParticipant> updateParticipantStatus(
            @PathVariable Long participantId,
            @RequestBody UpdateParticipantStatusRequest request) {
        try {
            MemoParticipant participant = memoService.updateParticipantStatus(
                    participantId,
                    request.getStatus()
            );
            return ResponseEntity.ok(participant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 메모 답장/채팅 목록 조회
    @GetMapping("/{memoId}/replies")
    public ResponseEntity<List<MemoReply>> getMemoReplies(@PathVariable Long memoId) {
        List<MemoReply> replies = memoService.getMemoReplies(memoId);
        return ResponseEntity.ok(replies);
    }
    
    // 메모 참여자 목록 조회
    @GetMapping("/{memoId}/participants")
    public ResponseEntity<List<MemoParticipant>> getMemoParticipants(@PathVariable Long memoId) {
        List<MemoParticipant> participants = memoService.getMemoParticipants(memoId);
        return ResponseEntity.ok(participants);
    }
    
    // 메모 삭제
    @DeleteMapping("/{memoId}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long memoId, @RequestParam Long userId) {
        try {
            memoService.deleteMemo(memoId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTO 클래스들
    public static class CreateMemoRequest {
        private Long userId;
        private String content;
        private String imageUrl;
        private Memo.MemoType type;
        private String location;
        private Integer maxParticipants;
        private String contactInfo;
        private LocalDateTime deadline;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Memo.MemoType getType() { return type; }
        public void setType(Memo.MemoType type) { this.type = type; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getMaxParticipants() { return maxParticipants; }
        public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        public LocalDateTime getDeadline() { return deadline; }
        public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    }
    
    public static class CreateMemoReplyRequest {
        private Long userId;
        private String content;
        private String imageUrl;
        private MemoReply.ReplyType type;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public MemoReply.ReplyType getType() { return type; }
        public void setType(MemoReply.ReplyType type) { this.type = type; }
    }
    
    public static class ParticipateRequest {
        private Long userId;
        private String message;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class UpdateParticipantStatusRequest {
        private MemoParticipant.ParticipantStatus status;
        
        // Getters and Setters
        public MemoParticipant.ParticipantStatus getStatus() { return status; }
        public void setStatus(MemoParticipant.ParticipantStatus status) { this.status = status; }
    }
}
