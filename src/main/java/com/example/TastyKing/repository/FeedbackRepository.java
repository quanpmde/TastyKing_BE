package com.example.TastyKing.repository;

import com.example.TastyKing.entity.Feedback;
import com.example.TastyKing.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    Optional<Feedback> findByOrderOrderID(Long orderID);

    Optional<Feedback> findByOrder(Order order);
}
