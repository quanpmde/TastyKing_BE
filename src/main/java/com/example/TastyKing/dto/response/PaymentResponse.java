package com.example.TastyKing.dto.response;

import com.example.TastyKing.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentResponse {
    private int paymentID;
    private Long orderId;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentDescription;
    private Date paymentDate;
    private int paymentAmount;
    private String paymentUrl; // Thêm trường này để chứa URL thanh toán
}
