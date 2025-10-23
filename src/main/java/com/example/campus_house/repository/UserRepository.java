package com.example.campus_house.repository;

import com.example.campus_house.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByUserId(Long userId);
    
    Optional<User> findByNickname(String nickname);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByNickname(String nickname);

    // 거주지 인증된 사용자 중 특정 건물에 인증된 사용자 조회
    java.util.List<User> findByVerifiedBuildingIdAndIsVerifiedTrue(Long verifiedBuildingId);
}
