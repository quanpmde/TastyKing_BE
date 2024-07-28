package com.example.TastyKing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RefundResponse {
    private int refundID;
    private Long orderID;
    private String refundBankAccount;
    private String refundBankName;
    private String refundBankAccountOwner;
    private int refundAmount;
    private String refundStatus;
    private Date refundDate;
    private String refundImage;
    private String refundDescription;
}