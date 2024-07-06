package com.example.TastyKing.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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


    @Column(name = "orderStatus", length = 50)
    private String orderStatus;

    @Column(name = "orderDate", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "totalAmount", nullable = false)
    private Double totalAmount;

    @Column(name = "numOfCustomer")
    private int numOfCustomer;

    @Column(name = "customerName", length = 255)
    private String customerName;

    @Column(name = "bookingDate")
    private LocalDateTime bookingDate;

    @Column(name = "customerPhone", length = 255)
    private String customerPhone;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}
