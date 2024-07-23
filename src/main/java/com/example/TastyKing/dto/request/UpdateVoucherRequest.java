package com.example.TastyKing.dto.request;

import com.example.TastyKing.validate.OpenDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateVoucherRequest {


    private String updateVoucherTitle;
    private int updateDiscount;
    private int updateQuantity;
    private Double updateExchangePoint;

    private LocalDateTime updateOpenDate;

    private LocalDateTime updateEndDate;
    private MultipartFile updateVoucherImage;
            private String updateDescription;
}
