package com.example.TastyKing.dto.response;

import com.example.TastyKing.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderResponse {
    private Long orderID;
    private User user;
    private Tables tables;
    private LocalDateTime orderDate;
    private String note;
    private Double totalAmount;
    private int numOfCustomer;
    private String customerName;
    private LocalDateTime bookingDate;
    private String customerPhone;
    private String orderStatus;
    private Double deposit;
    private List<OrderDetailResponse> orderDetails;
}
