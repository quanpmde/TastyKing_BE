package com.example.TastyKing.dto.request;

import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.validate.RatingConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ReviewRequest {
    private User user;
    private Food food;
    @NotNull
    private String reviewText;

    @RatingConstraint(message = "RATING_INVALID")
   private int rating;
}
