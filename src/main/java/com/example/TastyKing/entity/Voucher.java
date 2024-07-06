package com.example.TastyKing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "voucher", uniqueConstraints = @UniqueConstraint(columnNames = "VoucherId"))
public class Voucher {
    @Id
    @Column(name = "VoucherId", length = 50, unique = true)
    private String voucherId;

    @Column(name = "VoucherTitle", nullable = false, length = 100)
    private String voucherTitle;

    @Column(name = "VoucherDiscount", nullable = false)
    private int voucherDiscount;

    @Column(name = "VoucherQuantity", nullable = false)
    private int voucherQuantity;

    @Column(name = "VoucherUsed", nullable = false)
    private int numberVoucherUsed;

    @Column(name = "VoucherExchangeValue", nullable = false)
    private Double voucherExchangePoint;

    @Column(name = "VoucherStartDate", nullable = false)
    private LocalDateTime voucherStartDate;

    @Column(name = "VoucherDueDate", nullable = false)
    private LocalDateTime voucherDueDate;

    @Column(name = "VoucherImage", length = 2000)
    private String voucherImage;

    @Column(name = "VoucherDescribe", columnDefinition = "TEXT")
    private String voucherDescribe;

    @Column(name = "IsExpired")
    private boolean isExpired;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VoucherExchange> voucherExchanges;

}
