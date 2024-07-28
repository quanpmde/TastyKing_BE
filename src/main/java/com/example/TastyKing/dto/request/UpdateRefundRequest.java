package com.example.TastyKing.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRefundRequest {
    private String refundBankAccount;
    private String refundBankName;
    private String refundBankAccountOwner;
    private int refundAmount;
    private Date refundDate;
    private MultipartFile refundImage;
    private String refundDescription;
}