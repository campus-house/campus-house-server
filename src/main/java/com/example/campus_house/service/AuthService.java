package com.example.campus_house.service;

import com.example.campus_house.entity.User;
import com.example.campus_house.repository.UserRepository;
import com.example.campus_house.util.JwtUtil;
import com.example.campus_house.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.util.Optional; // 현재 사용하지 않음

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    
    // 회원가입
    @Transactional
    public User register(String email, String password, String nickname, User.UserType userType, 
                        String location, String university, String major) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        
        // 닉네임 중복 확인
        if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordUtil.encodePassword(password);
        
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .userType(userType)
                .location(location)
                .university(university)
                .major(major)
                .status(User.UserStatus.ACTIVE)
                .build();
        
        return userRepository.save(user);
    }
    
    // 로그인
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다."));
        
        // 비밀번호 확인
        if (!passwordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        
        // 계정 상태 확인
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("비활성화된 계정입니다.");
        }
        
        // JWT 토큰 생성
        return jwtUtil.generateToken(email, user.getId());
    }
    
    // 토큰으로 사용자 정보 조회
    public User getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        
        String email = jwtUtil.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
    
    // 이메일 중복 확인
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    // 닉네임 중복 확인
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }
    
    // 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 현재 비밀번호 확인
        if (!passwordUtil.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }
        
        // 새 비밀번호 암호화 및 저장
        String encodedNewPassword = passwordUtil.encodePassword(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }
    
    // 프로필 수정
    @Transactional
    public User updateProfile(Long userId, String nickname, String location, 
                            String university, String major, String introduction) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 닉네임 중복 확인 (본인 제외)
        if (!user.getNickname().equals(nickname) && userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
        
        user.setNickname(nickname);
        user.setLocation(location);
        user.setUniversity(university);
        user.setMajor(major);
        user.setIntroduction(introduction);
        
        return userRepository.save(user);
    }
}
