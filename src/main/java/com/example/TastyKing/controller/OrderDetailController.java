package com.example.TastyKing.controller;

import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.FoodResponse;
import com.example.TastyKing.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/orderDetails")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/top5")
    public ApiResponse<List<FoodResponse>> getTopOrderDetail() {
        return ApiResponse.<List<FoodResponse>>builder()
                .result(orderDetailService.getTopOrderDetail())
                .build();
    }
    }