package com.example.campus_house.controller;

import com.example.campus_house.entity.Character;
// import com.example.campus_house.entity.UserCharacter; // 현재 사용하지 않음
import com.example.campus_house.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {
    
    private final CharacterService characterService;
    
    // 모든 활성화된 캐릭터 조회
    @GetMapping
    public ResponseEntity<List<Character>> getAllCharacters() {
        List<Character> characters = characterService.getAllActiveCharacters();
        return ResponseEntity.ok(characters);
    }
    
    // 희귀도별 캐릭터 조회
    @GetMapping("/rarity/{rarity}")
    public ResponseEntity<List<Character>> getCharactersByRarity(@PathVariable String rarity) {
        try {
            Character.CharacterRarity rarityEnum = Character.CharacterRarity.valueOf(rarity.toUpperCase());
            List<Character> characters = characterService.getCharactersByRarity(rarityEnum);
            return ResponseEntity.ok(characters);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 캐릭터 상세 조회
    @GetMapping("/{characterId}")
    public ResponseEntity<Character> getCharacter(@PathVariable Long characterId) {
        // CharacterRepository에서 findById 메서드를 사용할 수 있도록 수정 필요
        return ResponseEntity.notFound().build();
    }
}
