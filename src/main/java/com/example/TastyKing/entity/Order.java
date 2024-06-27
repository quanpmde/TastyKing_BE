package com.example.TastyKing.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderID;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tableID", nullable = false)
    private Tables table;

    @ManyToOne
    @JoinColumn(name = "voucherId", nullable = true)
    private Voucher voucher;

    @Column(name = "orderStatus", length = 50)
    private String orderStatus;

    @Column(name = "orderDate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "totalAmount", nullable = false)
    private Double totalAmount;

    @Column(name = "numOfCustomer", nullable = false)
    private int numOfCustomer;

    @Column(name = "customerName", nullable = false, length = 255)
    private String customerName;

    @Column(name = "bookingDate", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "customerPhone", nullable = false, length = 255)
    private String customerPhone;
}
