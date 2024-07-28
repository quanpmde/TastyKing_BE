package com.example.TastyKing.dto.request;

import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.Order;
import com.example.TastyKing.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class FeedbackRequest {
    private User user;
    private Order order;
    @NotNull
    private String content;
        private String emotion;
}
