package com.example.TastyKing.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "voucherexchange")
public class VoucherExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VoucherExchangeID")
    Long voucherExchangeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VoucherID", nullable = false)
    Voucher voucher;

    @Column(name = "ExchangeDate", nullable = false)
    LocalDateTime exchangeDate;


}
