package com.example.campus_house.repository;

import com.example.campus_house.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    @Query("SELECT ub FROM UserBadge ub WHERE ub.user.userId = :userId AND ub.badgeType = :badgeType")
    Optional<UserBadge> findByUserIdAndBadgeType(@Param("userId") Long userId, @Param("badgeType") UserBadge.BadgeType badgeType);
}


