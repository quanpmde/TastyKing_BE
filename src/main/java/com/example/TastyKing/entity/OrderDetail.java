package com.example.TastyKing.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "orderDetail")
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "orderid")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("foodId")
    @JoinColumn(name = "foodid")
    private Food food;

    private int quantity;


}
