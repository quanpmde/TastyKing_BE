package com.example.TastyKing.dto.response;

import com.example.TastyKing.entity.User;
import com.example.TastyKing.entity.Voucher;
import com.example.TastyKing.entity.VoucherExchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class VoucherExchangeResponse {
    private Long voucherExchangeId;
    private User user;
    private Voucher voucher;
    private LocalDateTime voucherExchangeDate;
}
