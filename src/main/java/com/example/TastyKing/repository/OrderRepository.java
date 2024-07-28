package com.example.TastyKing.repository;

import com.example.TastyKing.entity.Order;
import com.example.TastyKing.entity.Tables;
import com.example.TastyKing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_email(String email);
    List<Order> findByUser_emailOrderByOrderIDDesc(String email);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'Done' AND o.orderDate BETWEEN :start AND :end")
    long countOrdersByStatusAndDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus = 'Done' AND o.orderDate BETWEEN :start AND :end")
    Double sumTotalAmountByStatusAndDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);



    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.totalAmount) " +
            "FROM Order o " +
            "WHERE o.orderStatus = 'Done' AND o.orderDate BETWEEN :start AND :end " +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)")
    List<Object[]> findMonthlyTotalAmountByStatusAndDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderID = :orderID")
    Double findTotalAmountByOrderID(@Param("orderID") Long orderID);
    @Query("SELECT o FROM Order o ORDER BY o.orderID DESC")
    List<Order> findAllOrderByOrderIDDesc();

    List<Order> findAllByOrderStatusAndBookingDateBefore(String orderStatus, LocalDateTime bookingDate);
    boolean existsByUserAndOrderStatus(User user, String orderStatus);

    List<Order> findByTableAndBookingDateBetween(Tables tables, LocalDateTime twoHoursBefore, LocalDateTime twoHoursAfter);
}

