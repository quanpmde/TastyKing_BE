package com.example.TastyKing.dto.request;

import com.example.TastyKing.entity.Tables;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.validate.OpenDateConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderUpdateRequest {
    private Long orderID;
    @NotNull(message = "User must not be null")
    private User user;
    @NotNull(message = "Table must not be null")
    private Tables tables;

    private LocalDateTime orderDate;
    private String note;
    @NotNull(message = "Total amount must not be null")
    private Double totalAmount;
    private int numOfCustomer;
    private String customerName;

    private LocalDateTime bookingDate;
    @Size(min = 10, message = "PHONE_INVALID")
    private String customerPhone;

    private List<OrderDetailRequest> orderDetails;
}
