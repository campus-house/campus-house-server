package com.example.campus_house.service;

import com.example.campus_house.entity.Character;
import com.example.campus_house.entity.PointHistory;
import com.example.campus_house.entity.User;
import com.example.campus_house.entity.UserCharacter;
import com.example.campus_house.repository.CharacterRepository;
import com.example.campus_house.repository.PointHistoryRepository;
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
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    // 모든 활성화된 캐릭터 조회
    public List<Character> getAllActiveCharacters() {
        return characterRepository.findByIsActiveTrueOrderByRarityAscPriceAsc();
    }
    
    // 희귀도별 캐릭터 조회
    public List<Character> getCharactersByRarity(Character.CharacterRarity rarity) {
        return characterRepository.findByRarityAndIsActiveTrueOrderByPriceAsc(rarity);
    }
    
    // 사용자별 보유 캐릭터 조회
    public List<UserCharacter> getUserCharacters(Long userId) {
        return userCharacterRepository.findByUserIdOrderByObtainedAtDesc(userId);
    }
    
    // 사용자의 대표 캐릭터 조회
    public Optional<UserCharacter> getMainCharacter(Long userId) {
        return userCharacterRepository.findMainCharacterByUserId(userId);
    }
    
    // 캐릭터 가챠 (뽑기)
    @Transactional
    public Character performGacha(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 가챠 비용 확인 (100 포인트)
        int gachaCost = 100;
        if (user.getPoints() < gachaCost) {
            throw new RuntimeException("포인트가 부족합니다. (필요: " + gachaCost + " 포인트)");
        }
        
        // 가챠 실행
        Character gachaResult = performGachaLogic();
        
        // 사용자 캐릭터 보유 처리
        Optional<UserCharacter> existingUserCharacter = userCharacterRepository.findByUserIdAndCharacterId(userId, gachaResult.getId());
        if (existingUserCharacter.isPresent()) {
            // 이미 보유한 캐릭터인 경우 수량 증가
            UserCharacter userCharacter = existingUserCharacter.get();
            userCharacter.setQuantity(userCharacter.getQuantity() + 1);
            userCharacterRepository.save(userCharacter);
        } else {
            // 새로운 캐릭터인 경우 새로 생성
            UserCharacter newUserCharacter = UserCharacter.builder()
                    .user(user)
                    .character(gachaResult)
                    .isMain(false)
                    .quantity(1)
                    .obtainedAt(LocalDateTime.now())
                    .build();
            userCharacterRepository.save(newUserCharacter);
        }
        
        // 포인트 차감
        user.setPoints(user.getPoints() - gachaCost);
        userRepository.save(user);
        
        // 포인트 내역 기록
        PointHistory pointHistory = PointHistory.builder()
                .user(user)
                .type(PointHistory.PointType.CHARACTER_GACHA)
                .amount(-gachaCost)
                .balance(user.getPoints())
                .description("캐릭터 가챠 (" + gachaResult.getName() + ")")
                .relatedId(gachaResult.getId().toString())
                .build();
        pointHistoryRepository.save(pointHistory);
        
        // 캐릭터 획득 알림
        notificationService.createNotification(
                userId,
                com.example.campus_house.entity.Notification.NotificationType.CHARACTER_OBTAINED,
                "새로운 캐릭터를 획득했습니다!",
                gachaResult.getName() + " 캐릭터를 획득했습니다! (" + gachaResult.getRarity() + " 등급)",
                gachaResult.getId().toString(),
                "CHARACTER"
        );
        
        return gachaResult;
    }
    
    // 가챠 로직 (희귀도별 확률 적용)
    private Character performGachaLogic() {
        List<Character> allCharacters = characterRepository.findByIsActiveTrueOrderByRarityAscPriceAsc();
        if (allCharacters.isEmpty()) {
            throw new RuntimeException("가챠할 수 있는 캐릭터가 없습니다.");
        }
        
        Random random = new Random();
        int randomValue = random.nextInt(100) + 1; // 1-100
        
        Character.CharacterRarity selectedRarity;
        if (randomValue <= 2) {
            selectedRarity = Character.CharacterRarity.LEGENDARY; // 2%
        } else if (randomValue <= 10) {
            selectedRarity = Character.CharacterRarity.EPIC; // 8%
        } else if (randomValue <= 30) {
            selectedRarity = Character.CharacterRarity.RARE; // 20%
        } else {
            selectedRarity = Character.CharacterRarity.COMMON; // 70%
        }
        
        // 선택된 희귀도의 캐릭터 중 랜덤 선택
        List<Character> charactersByRarity = characterRepository.findByRarityAndIsActiveTrueOrderByPriceAsc(selectedRarity);
        if (charactersByRarity.isEmpty()) {
            // 해당 희귀도에 캐릭터가 없으면 일반 등급으로 폴백
            charactersByRarity = characterRepository.findByRarityAndIsActiveTrueOrderByPriceAsc(Character.CharacterRarity.COMMON);
        }
        
        return charactersByRarity.get(random.nextInt(charactersByRarity.size()));
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
        
        // 포인트 확인
        if (user.getPoints() < character.getPrice()) {
            throw new RuntimeException("포인트가 부족합니다. (필요: " + character.getPrice() + " 포인트)");
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
        
        // 포인트 차감
        user.setPoints(user.getPoints() - character.getPrice());
        userRepository.save(user);
        
        // 포인트 내역 기록
        PointHistory pointHistory = PointHistory.builder()
                .user(user)
                .type(PointHistory.PointType.CHARACTER_PURCHASE)
                .amount(-character.getPrice())
                .balance(user.getPoints())
                .description("캐릭터 구매 (" + character.getName() + ")")
                .relatedId(character.getId().toString())
                .build();
        pointHistoryRepository.save(pointHistory);
        
        return character;
    }
    
    // 사용자 통계 조회
    public UserCharacterStats getUserCharacterStats(Long userId) {
        Long totalCharacters = userCharacterRepository.countByUserId(userId);
        Long commonCount = userCharacterRepository.countByUserIdAndRarity(userId, "COMMON");
        Long rareCount = userCharacterRepository.countByUserIdAndRarity(userId, "RARE");
        Long epicCount = userCharacterRepository.countByUserIdAndRarity(userId, "EPIC");
        Long legendaryCount = userCharacterRepository.countByUserIdAndRarity(userId, "LEGENDARY");
        
        return UserCharacterStats.builder()
                .totalCharacters(totalCharacters)
                .commonCount(commonCount)
                .rareCount(rareCount)
                .epicCount(epicCount)
                .legendaryCount(legendaryCount)
                .build();
    }
    
    // DTO 클래스
    @lombok.Data
    @lombok.Builder
    public static class UserCharacterStats {
        private Long totalCharacters;
        private Long commonCount;
        private Long rareCount;
        private Long epicCount;
        private Long legendaryCount;
    }
}
