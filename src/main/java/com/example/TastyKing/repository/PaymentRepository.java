package com.example.TastyKing.repository;

import com.example.TastyKing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByPaymentStatus(String paymentStatus);

    // Truy vấn các payment theo thứ tự tạo mới nhất
    List<Payment> findAllByOrderByPaymentDateDesc();

    // Truy vấn các payment theo status và thứ tự tạo mới nhất
    List<Payment> findByPaymentStatusOrderByPaymentDateDesc(String paymentStatus);

    Optional<Payment> findByOrderOrderID(Long orderID);
    @Query("SELECT YEAR(p.paymentDate), MONTH(p.paymentDate), SUM(p.paymentAmount) " +
            "FROM Payment p " +
            "WHERE p.paymentStatus = 'PAID' AND p.paymentDate BETWEEN :start AND :end " +
            "GROUP BY YEAR(p.paymentDate), MONTH(p.paymentDate)")
    List<Object[]> findMonthlyTotalPaymentAmountByStatusAndDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
