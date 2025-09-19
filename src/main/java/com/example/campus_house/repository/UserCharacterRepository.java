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
    List<UserCharacter> findByUserIdOrderByObtainedAtDesc(Long userId);
    
    // 사용자의 특정 캐릭터 보유 여부 확인
    Optional<UserCharacter> findByUserIdAndCharacterId(Long userId, Long characterId);
    
    // 사용자의 대표 캐릭터 조회
    @Query("SELECT uc FROM UserCharacter uc WHERE uc.user.id = :userId AND uc.isMain = true")
    Optional<UserCharacter> findMainCharacterByUserId(@Param("userId") Long userId);
    
    // 사용자의 캐릭터 보유 수량 조회
    @Query("SELECT COUNT(uc) FROM UserCharacter uc WHERE uc.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    // 희귀도별 보유 캐릭터 수 조회
    @Query("SELECT COUNT(uc) FROM UserCharacter uc WHERE uc.user.id = :userId AND uc.character.rarity = :rarity")
    Long countByUserIdAndRarity(@Param("userId") Long userId, @Param("rarity") String rarity);
}
