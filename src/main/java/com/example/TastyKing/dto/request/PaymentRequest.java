package com.example.TastyKing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentRequest {
    private int paymentID;
    private Long orderID;
    private String paymentMethod;
    private String paymentDescription;
    private String paymentStatus;
    private Date paymentDate;
    private Integer paymentAmount;
}
