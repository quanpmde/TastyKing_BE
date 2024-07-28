package com.example.TastyKing.dto.response;

import com.example.TastyKing.entity.Order;
import com.example.TastyKing.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class FeedbackResponse {
    private Integer feedbackId;
    private User user;
    private Long orderID;
    private String content;
    private String emotion;
    private LocalDateTime feedbackDate;
}
