package com.example.TastyKing.repository;

import com.example.TastyKing.entity.User;
import com.example.TastyKing.entity.VoucherExchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherExchangeRepository extends JpaRepository<VoucherExchange, Long> {
    List<VoucherExchange> findByUser(User user);
}
