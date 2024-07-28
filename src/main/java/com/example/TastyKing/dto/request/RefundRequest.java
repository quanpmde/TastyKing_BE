package com.example.TastyKing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RefundRequest {
    private int refundID;
    private Long orderID;
    private String refundBankAccount;
    private String refundBankName;
    private String refundBankAccountOwner;
    private int refundAmount;
    private Date refundDate;
    private MultipartFile refundImage;
    private String refundDescription;

}