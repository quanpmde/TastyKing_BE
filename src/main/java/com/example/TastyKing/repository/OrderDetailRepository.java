package com.example.TastyKing.repository;

import com.example.TastyKing.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT od.food, SUM(od.quantity) as totalQuantity FROM OrderDetail od GROUP BY od.food ORDER BY totalQuantity DESC")
    List<Object[]> findTopOrderedFoods();
}
