package com.example.TastyKing.dto.request;

import com.example.TastyKing.entity.User;
import com.example.TastyKing.entity.Voucher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class VoucherExchangeRequest {
    private User user;
    private Voucher voucher;

}
