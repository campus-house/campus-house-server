package com.example.campus_house.service;

import com.example.campus_house.entity.User;
import com.example.campus_house.entity.UserBadge;
import com.example.campus_house.repository.UserBadgeRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void awardIfFirstPost(Long userId) {
        awardIfAbsent(userId, UserBadge.BadgeType.FIRST_POST);
    }

    @Transactional
    public void awardIfFirstLike(Long userId) {
        awardIfAbsent(userId, UserBadge.BadgeType.FIRST_LIKE);
    }

    @Transactional
    public void awardIfFirstComment(Long userId) {
        awardIfAbsent(userId, UserBadge.BadgeType.FIRST_COMMENT);
    }

    private void awardIfAbsent(Long userId, UserBadge.BadgeType badgeType) {
        if (userBadgeRepository.findByUserIdAndBadgeType(userId, badgeType).isPresent()) {
            return; // 이미 보유
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        UserBadge badge = UserBadge.builder()
                .user(user)
                .badgeType(badgeType)
                .build();
        userBadgeRepository.save(badge);
    }
}


