package com.example.TastyKing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailId implements Serializable {
    @Column(name = "orderid")
    private Long orderId;

    @Column(name = "foodId")
    private Long foodId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(foodId, that.foodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, foodId);
    }
}
