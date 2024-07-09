package com.example.TastyKing.controller;

import com.example.TastyKing.dto.request.ReviewRequest;
import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.ReviewResponse;
import com.example.TastyKing.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest reviewRequest){
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(reviewRequest)).build();
    }


    @DeleteMapping("/{reviewID}")
    public ApiResponse<String> deleteReview(@PathVariable("reviewID") Integer reviewID){
        return ApiResponse.<String>builder()
                .result(reviewService.deleteReview(reviewID))
                .build();
    }
}
