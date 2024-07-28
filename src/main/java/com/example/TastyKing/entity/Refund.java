package com.example.TastyKing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "Refund")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int refundID;

    @OneToOne
    @JoinColumn(name = "orderID")
    private Order order;

    @Column(name = "refundBankAccountOwner",length = 40)
    private String refundBankAccountOwner;

    @Column(name = "refundBankAccount",length = 40)
    private String refundBankAccount;

    @Column(name = "refundBankName",length = 40)
    private String refundBankName;

    @Column(name = "refundAmount",nullable = false)
    private int refundAmount;

    @Column(name = "refundStatus")
    private String refundStatus;

    @Column(name = "refundImage",length = 1000)
    private String refundImage;

    @Column(name = "refundDate")
    private Date refundDate;

    @Column(name = "refundDescription",length = 1000)
    private String refundDescription;

}