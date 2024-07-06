package com.example.TastyKing.dto.response;

import com.example.TastyKing.entity.Food;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderDetailResponse {
    private Long foodID;
    private String foodName;
    private Double foodPrice;
    private String foodImage;
    private int quantity;
}
