package com.example.TastyKing.repository;

import com.example.TastyKing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByPaymentStatus(String paymentStatus);

    // Truy vấn các payment theo thứ tự tạo mới nhất
    List<Payment> findAllByOrderByPaymentDateDesc();

    // Truy vấn các payment theo status và thứ tự tạo mới nhất
    List<Payment> findByPaymentStatusOrderByPaymentDateDesc(String paymentStatus);
}
