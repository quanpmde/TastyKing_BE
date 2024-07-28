package com.example.TastyKing.repository;

import com.example.TastyKing.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Integer> {
    Optional<Refund> findByOrderOrderID(Long orderID);

    List<Refund> findAllByOrderByRefundDateDesc();
}
