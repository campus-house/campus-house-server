package com.example.campus_house.service;

import com.example.campus_house.entity.Character;
import com.example.campus_house.entity.RewardHistory;
import com.example.campus_house.entity.User;
import com.example.campus_house.entity.UserCharacter;
import com.example.campus_house.repository.CharacterRepository;
import com.example.campus_house.repository.RewardHistoryRepository;
import com.example.campus_house.repository.UserCharacterRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CharacterService {
    
    private final CharacterRepository characterRepository;
    private final UserCharacterRepository userCharacterRepository;
    private final RewardHistoryRepository rewardHistoryRepository;
    private final UserRepository userRepository;
    
    // 모든 활성화된 캐릭터 조회
    public List<Character> getAllActiveCharacters() {
        return characterRepository.findByIsActiveTrueOrderByPriceAsc();
    }
    
    // 사용자별 보유 캐릭터 조회
    public List<UserCharacter> getUserCharacters(Long userId) {
        return userCharacterRepository.findByUserIdOrderByObtainedAtDesc(userId);
    }
    
    // 사용자의 대표 캐릭터 조회
    public Optional<UserCharacter> getMainCharacter(Long userId) {
        return userCharacterRepository.findMainCharacterByUserId(userId);
    }
    
    // 캐릭터 가챠 (뽑기) - 나중에 구현 예정
    @Transactional
    public Character performGacha(Long userId) {
        throw new RuntimeException("가챠 기능은 나중에 구현될 예정입니다.");
    }
    
    
    // 대표 캐릭터 설정
    @Transactional
    public void setMainCharacter(Long userId, Long characterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        UserCharacter userCharacter = userCharacterRepository.findByUserIdAndCharacterId(userId, characterId)
                .orElseThrow(() -> new RuntimeException("보유하지 않은 캐릭터입니다."));
        
        // 기존 대표 캐릭터 해제
        Optional<UserCharacter> currentMain = userCharacterRepository.findMainCharacterByUserId(userId);
        if (currentMain.isPresent()) {
            currentMain.get().setIsMain(false);
            userCharacterRepository.save(currentMain.get());
        }
        
        // 새 대표 캐릭터 설정
        userCharacter.setIsMain(true);
        userCharacterRepository.save(userCharacter);
        
        // 사용자 정보 업데이트
        user.setMainCharacterId(characterId);
        userRepository.save(user);
    }
    
    // 캐릭터 구매
    @Transactional
    public Character purchaseCharacter(Long userId, Long characterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("캐릭터를 찾을 수 없습니다."));
        
        if (!character.getIsActive()) {
            throw new RuntimeException("판매하지 않는 캐릭터입니다.");
        }
        
        // 리워드 확인
        if (user.getRewards() < character.getPrice()) {
            throw new RuntimeException("리워드가 부족합니다. (필요: " + character.getPrice() + " 리워드)");
        }
        
        // 사용자 캐릭터 보유 처리
        Optional<UserCharacter> existingUserCharacter = userCharacterRepository.findByUserIdAndCharacterId(userId, characterId);
        if (existingUserCharacter.isPresent()) {
            // 이미 보유한 캐릭터인 경우 수량 증가
            UserCharacter userCharacter = existingUserCharacter.get();
            userCharacter.setQuantity(userCharacter.getQuantity() + 1);
            userCharacterRepository.save(userCharacter);
        } else {
            // 새로운 캐릭터인 경우 새로 생성
            UserCharacter newUserCharacter = UserCharacter.builder()
                    .user(user)
                    .character(character)
                    .isMain(false)
                    .quantity(1)
                    .obtainedAt(LocalDateTime.now())
                    .build();
            userCharacterRepository.save(newUserCharacter);
        }
        
        // 리워드 차감
        user.setRewards(user.getRewards() - character.getPrice());
        userRepository.save(user);
        
        // 리워드 내역 기록
        RewardHistory rewardHistory = RewardHistory.builder()
                .user(user)
                .type(RewardHistory.RewardType.CHARACTER_PURCHASE)
                .amount(-character.getPrice())
                .balance(user.getRewards())
                .description("캐릭터 구매 (" + character.getName() + ")")
                .relatedId(character.getId().toString())
                .build();
        rewardHistoryRepository.save(rewardHistory);
        
        return character;
    }
    
    // 사용자 통계 조회
    public UserCharacterStats getUserCharacterStats(Long userId) {
        Long totalCharacters = userCharacterRepository.countByUserId(userId);
        
        return UserCharacterStats.builder()
                .totalCharacters(totalCharacters)
                .build();
    }
    
    // DTO 클래스
    @lombok.Data
    @lombok.Builder
    public static class UserCharacterStats {
        private Long totalCharacters;
    }
}
