package com.example.campus_house.controller;

import com.example.campus_house.dto.ApiResponse;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일/닉네임")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody RegisterRequest request) {
        User user = authService.register(
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getUserType(),
                request.getLocation(),
                request.getUniversity(),
                request.getMajor()
        );
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("email", user.getEmail());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("userType", user.getUserType());
        userInfo.put("location", user.getLocation());
        userInfo.put("university", user.getUniversity());
        userInfo.put("major", user.getMajor());
        
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", userInfo));
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            
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
            
            User user = authService.getUserWithProfileImage(authService.getUserFromToken(token).getUserId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("email", user.getEmail());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("userType", user.getUserType());
            userInfo.put("location", user.getLocation());
            userInfo.put("university", user.getUniversity());
            userInfo.put("major", user.getMajor());
            userInfo.put("profileImage", user.getEffectiveProfileImage());
            userInfo.put("mainCharacterId", user.getMainCharacterId());
            
            response.put("user", userInfo);
            
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
    
    // 아이디 중복 확인
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        boolean isAvailable = authService.isUsernameAvailable(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.");
        
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
            authService.changePassword(user.getUserId(), request.getCurrentPassword(), request.getNewPassword());
            
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
                    user.getUserId(),
                    request.getNickname(),
                    request.getLocation(),
                    request.getUniversity(),
                    request.getMajor(),
                    request.getIntroduction()
            );
            
            // 프로필 이미지 정보 포함
            User userWithProfileImage = authService.getUserWithProfileImage(updatedUser.getUserId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "프로필이 수정되었습니다.");
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userWithProfileImage.getUserId());
            userInfo.put("email", userWithProfileImage.getEmail());
            userInfo.put("username", userWithProfileImage.getUsername());
            userInfo.put("nickname", userWithProfileImage.getNickname());
            userInfo.put("userType", userWithProfileImage.getUserType());
            userInfo.put("location", userWithProfileImage.getLocation());
            userInfo.put("university", userWithProfileImage.getUniversity());
            userInfo.put("major", userWithProfileImage.getMajor());
            userInfo.put("introduction", userWithProfileImage.getIntroduction());
            userInfo.put("profileImage", userWithProfileImage.getEffectiveProfileImage());
            userInfo.put("mainCharacterId", userWithProfileImage.getMainCharacterId());
            
            response.put("user", userInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 대표 캐릭터 설정
    @PostMapping("/set-main-character")
    public ResponseEntity<Map<String, Object>> setMainCharacter(
            @RequestHeader("Authorization") String token,
            @RequestBody SetMainCharacterRequest request) {
        try {
            // "Bearer " 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            User user = authService.getUserFromToken(token);
            User updatedUser = authService.setMainCharacter(user.getUserId(), request.getCharacterId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "대표 캐릭터가 설정되었습니다.");
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", updatedUser.getUserId());
            userInfo.put("mainCharacterId", updatedUser.getMainCharacterId());
            userInfo.put("profileImage", updatedUser.getEffectiveProfileImage());
            
            response.put("user", userInfo);
            
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
        private String username;
        private String password;
        private String nickname;
        private User.UserType userType;
        private String location;
        private String university;
        private String major;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
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
        private String username;
        private String password;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
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
    
    public static class SetMainCharacterRequest {
        private Long characterId;
        
        // Getters and Setters
        public Long getCharacterId() { return characterId; }
        public void setCharacterId(Long characterId) { this.characterId = characterId; }
    }
}
