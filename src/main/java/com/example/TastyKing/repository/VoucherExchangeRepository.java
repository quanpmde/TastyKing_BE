package com.example.TastyKing.repository;

import com.example.TastyKing.entity.User;
import com.example.TastyKing.entity.Voucher;
import com.example.TastyKing.entity.VoucherExchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoucherExchangeRepository extends JpaRepository<VoucherExchange, Long> {
    List<VoucherExchange> findByUser(User user);

    boolean existsByUserAndVoucher(User user, Voucher voucher);

    Optional<VoucherExchange> findByUserAndVoucher(User user, Voucher voucher);

    Optional<VoucherExchange> findTopByUserAndVoucherOrderByExchangeDateDesc(User user, Voucher voucher);
}
