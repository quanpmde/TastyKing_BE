package com.example.TastyKing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ComboResponse {
    private Long comboID;
    private String comboTitle;
    private Double newPrice;
    private LocalDateTime openDate;
    private LocalDateTime endDate;
    private String comboDescription;
    private String comboImage;

    private List<ComboFoodResponse> comboFoods;
}
