package com.example.TastyKing.repository;

import com.example.TastyKing.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    Optional<Voucher> findByVoucherCode(String voucherCode);

    boolean existsByVoucherCode(String voucherCode);
}
