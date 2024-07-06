package com.example.TastyKing.repository;

import com.example.TastyKing.entity.RewardPoint;
import com.example.TastyKing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RewardPointRepository extends JpaRepository<RewardPoint, Integer> {
    Optional<RewardPoint> findByUser(User user);
}
