package com.example.TastyKing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentID;

    @OneToOne
    @JoinColumn(name = "orderID")
    private Order order;

    @Column(name = "PaymentMethod",nullable = false)
    private String paymentMethod;

    @Column(name = "PaymentDescription",columnDefinition = "TEXT")
    private String paymentDescription;

    @Column(name = "PaymentStatus")
    private String paymentStatus;

    @Column(name = "PaymentDate")
    private Date paymentDate;

    @Column(name = "PaymentAmount",nullable = false)
    private int paymentAmount;
}
