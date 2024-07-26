package com.example.TastyKing.dto.response;

import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ReviewResponse {
    private Integer reviewId;
    private User user;
    private String foodName;
    private String reviewText;
    private int rating;
    private LocalDateTime reviewDate;
}
