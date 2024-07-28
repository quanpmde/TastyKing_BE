package com.example.TastyKing.controller;

import com.example.TastyKing.dto.request.FeedbackRequest;
import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.FeedbackResponse;
import com.example.TastyKing.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ApiResponse<FeedbackResponse> createFeedback(@RequestBody FeedbackRequest feedbackRequest){
        return ApiResponse.<FeedbackResponse>builder()
                .result(feedbackService.createFeedback(feedbackRequest))
                .build();
    }

    @GetMapping("/getFeedbackOrder/{orderID}")
    public ApiResponse<FeedbackResponse> getFeedbackByOrder(@PathVariable("orderID") Long orderID){
        return ApiResponse.<FeedbackResponse>builder()
                .result(feedbackService.getFeedbackByOrderID(orderID))
                .build();
    }

    @GetMapping
    public ApiResponse<List<FeedbackResponse>> getAllFeedback(){
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getAllFeedback())
                .build();
    }

    @GetMapping("/{feedbackId}")
    public ApiResponse<FeedbackResponse> getFeedbackByFeedbackID(@PathVariable("feedbackId") Integer feedbackId){
        return ApiResponse.<FeedbackResponse>builder()
                .result(feedbackService.getFeedbackByFeedbackID(feedbackId))
                .build();
    }

    @DeleteMapping("/{feedbackId}")
    public ApiResponse<String> deleteFeedback(@PathVariable("feedbackId") Integer feedbackId){
        return ApiResponse.<String>builder()
                .result(feedbackService.deleteFeedback(feedbackId))
                .build();
    }
}

