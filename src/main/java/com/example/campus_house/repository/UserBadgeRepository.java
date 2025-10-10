package com.example.campus_house.repository;

import com.example.campus_house.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    Optional<UserBadge> findByUserIdAndBadgeType(Long userId, UserBadge.BadgeType badgeType);
}


