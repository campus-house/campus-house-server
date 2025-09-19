package com.example.campus_house.controller;

import com.example.campus_house.entity.User;
import com.example.campus_house.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인/회원가입 관련 API")
public class AuthController {
    
    private final AuthService authService;
    
    // 회원가입
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일/닉네임")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getNickname(),
                    request.getUserType(),
                    request.getLocation(),
                    request.getUniversity(),
                    request.getMajor()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "nickname", user.getNickname(),
                    "userType", user.getUserType()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인에 성공했습니다.");
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 토큰 검증
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            // "Bearer " 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            User user = authService.getUserFromToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "nickname", user.getNickname(),
                    "userType", user.getUserType(),
                    "location", user.getLocation(),
                    "university", user.getUniversity(),
                    "major", user.getMajor()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");
        
        return ResponseEntity.ok(response);
    }
    
    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = authService.isNicknameAvailable(nickname);
        
        Map<String, Object> response = new HashMap<>();
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.");
        
        return ResponseEntity.ok(response);
    }
    
    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request) {
        try {
            // "Bearer " 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            User user = authService.getUserFromToken(token);
            authService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "비밀번호가 변경되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileRequest request) {
        try {
            // "Bearer " 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            User user = authService.getUserFromToken(token);
            User updatedUser = authService.updateProfile(
                    user.getId(),
                    request.getNickname(),
                    request.getLocation(),
                    request.getUniversity(),
                    request.getMajor(),
                    request.getIntroduction()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "프로필이 수정되었습니다.");
            response.put("user", Map.of(
                    "id", updatedUser.getId(),
                    "email", updatedUser.getEmail(),
                    "nickname", updatedUser.getNickname(),
                    "userType", updatedUser.getUserType(),
                    "location", updatedUser.getLocation(),
                    "university", updatedUser.getUniversity(),
                    "major", updatedUser.getMajor(),
                    "introduction", updatedUser.getIntroduction()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // DTO 클래스들
    public static class RegisterRequest {
        private String email;
        private String password;
        private String nickname;
        private User.UserType userType;
        private String location;
        private String university;
        private String major;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public User.UserType getUserType() { return userType; }
        public void setUserType(User.UserType userType) { this.userType = userType; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getUniversity() { return university; }
        public void setUniversity(String university) { this.university = university; }
        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }
    }
    
    public static class LoginRequest {
        private String email;
        private String password;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        
        // Getters and Setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    public static class UpdateProfileRequest {
        private String nickname;
        private String location;
        private String university;
        private String major;
        private String introduction;
        
        // Getters and Setters
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getUniversity() { return university; }
        public void setUniversity(String university) { this.university = university; }
        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }
        public String getIntroduction() { return introduction; }
        public void setIntroduction(String introduction) { this.introduction = introduction; }
    }
}
