package com.example.TastyKing.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "voucherExchanges"})
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VoucherId", length = 50, unique = true)
    private int voucherId;

    @Column(name = "VoucherCode", nullable = false, length = 100)
    private String voucherCode;

    @Column(name = "VoucherTitle", nullable = false, length = 100)
    private String voucherTitle;

    @Column(name = "VoucherDiscount", nullable = false)
    private int voucherDiscount;

    @Column(name = "VoucherQuantity", nullable = false)
    private int voucherQuantity;

    @Column(name = "VoucherUsed", nullable = false)
    private int numberVoucherUsed;

    @Column(name = "VoucherExchangePoint", nullable = false)
    private Double voucherExchangePoint;

    @Column(name = "VoucherStartDate", nullable = false)
    private LocalDateTime voucherStartDate;

    @Column(name = "VoucherDueDate", nullable = false)
    private LocalDateTime voucherDueDate;

    @Column(name = "VoucherImage", length = 2000)
    private String voucherImage;

    @Column(name = "VoucherDescribe", columnDefinition = "TEXT")
    private String voucherDescribe;

    @Column(name = "Expried")
    private int expried;

    
}