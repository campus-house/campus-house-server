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
    public User register(String email, String username, String password, String nickname, User.UserType userType, 
                        String location, String university, String major) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        
        // 아이디 중복 확인
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
        
        // 닉네임 중복 확인
        if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordUtil.encodePassword(password);
        
        User user = User.builder()
                .email(email)
                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .userType(userType)
                .location(location)
                .university(university)
                .major(major)
                .build();
        
        return userRepository.save(user);
    }
    
    // 로그인 (아이디로 로그인)
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));
        
        // 비밀번호 확인
        if (!passwordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        
        // 계정 상태 확인
        
        // JWT 토큰 생성 (아이디와 사용자 ID 사용)
        return jwtUtil.generateToken(user.getUsername(), user.getUserId());
    }
    
    // 토큰으로 사용자 정보 조회
    public User getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
    
    // 이메일 중복 확인
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    // 아이디 중복 확인
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    // 닉네임 중복 확인
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }
    
    // 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findByUserId(userId)
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
        User user = userRepository.findByUserId(userId)
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
    
    /**
     * 대표 캐릭터 기반 프로필 이미지를 포함한 사용자 정보를 반환합니다.
     * 
     * @param userId 사용자 ID
     * @return 프로필 이미지가 설정된 사용자 정보
     */
    public User getUserWithProfileImage(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 대표 캐릭터가 설정된 경우 프로필 이미지를 캐릭터 이미지로 업데이트
        if (user.getMainCharacterId() != null) {
            String characterImageUrl = user.getEffectiveProfileImage();
            if (characterImageUrl != null && !characterImageUrl.equals(user.getProfileImage())) {
                user.setProfileImage(characterImageUrl);
            }
        }
        
        return user;
    }
    
    /**
     * 대표 캐릭터를 설정합니다.
     * 
     * @param userId 사용자 ID
     * @param characterId 캐릭터 ID
     * @return 업데이트된 사용자 정보
     */
    @Transactional
    public User setMainCharacter(Long userId, Long characterId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 사용자가 해당 캐릭터를 보유하고 있는지 확인
        boolean hasCharacter = user.getUserCharacters().stream()
                .anyMatch(uc -> uc.getCharacter().getId().equals(characterId));
        
        if (!hasCharacter) {
            throw new RuntimeException("보유하지 않은 캐릭터입니다.");
        }
        
        // 기존 대표 캐릭터 해제
        user.getUserCharacters().forEach(uc -> uc.setIsMain(false));
        
        // 새 대표 캐릭터 설정
        user.getUserCharacters().stream()
                .filter(uc -> uc.getCharacter().getId().equals(characterId))
                .findFirst()
                .ifPresent(uc -> {
                    uc.setIsMain(true);
                    user.setMainCharacterId(characterId);
                    // 프로필 이미지를 캐릭터 이미지로 업데이트
                    user.setProfileImage(uc.getCharacter().getImageUrl());
                });
        
        return userRepository.save(user);
    }
}
