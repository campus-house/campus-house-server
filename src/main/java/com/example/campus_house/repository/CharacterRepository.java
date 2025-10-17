package com.example.campus_house.repository;

import com.example.campus_house.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    
    // 활성화된 캐릭터 조회
    List<Character> findByIsActiveTrueOrderByPriceAsc();
    
    // 가격대별 캐릭터 조회
    List<Character> findByPriceBetweenAndIsActiveTrueOrderByPriceAsc(Integer minPrice, Integer maxPrice);
}
