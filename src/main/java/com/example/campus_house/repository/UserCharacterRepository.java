package com.example.campus_house.repository;

import com.example.campus_house.entity.UserCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {
    
    // 사용자별 보유 캐릭터 조회
    @Query("SELECT uc FROM UserCharacter uc WHERE uc.user.userId = :userId ORDER BY uc.obtainedAt DESC")
    List<UserCharacter> findByUserIdOrderByObtainedAtDesc(@Param("userId") Long userId);
    
    // 사용자의 특정 캐릭터 보유 여부 확인
    @Query("SELECT uc FROM UserCharacter uc WHERE uc.user.userId = :userId AND uc.character.id = :characterId")
    Optional<UserCharacter> findByUserIdAndCharacterId(@Param("userId") Long userId, @Param("characterId") Long characterId);
    
    // 사용자의 대표 캐릭터 조회
    @Query("SELECT uc FROM UserCharacter uc WHERE uc.user.userId = :userId AND uc.isMain = true")
    Optional<UserCharacter> findMainCharacterByUserId(@Param("userId") Long userId);
    
    // 사용자의 캐릭터 보유 수량 조회
    @Query("SELECT COUNT(uc) FROM UserCharacter uc WHERE uc.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
}
