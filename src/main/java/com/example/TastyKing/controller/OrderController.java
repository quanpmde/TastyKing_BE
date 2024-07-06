package com.example.TastyKing.controller;

import com.example.TastyKing.dto.request.OrderRequest;
import com.example.TastyKing.dto.request.OrderUpdateRequest;
import com.example.TastyKing.dto.request.UpdateOrderStatusRequest;
import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.OrderResponse;
import com.example.TastyKing.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request){
        logger.info("Received order request: {}", request);
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }
    @GetMapping("/{orderID}")
    public ApiResponse<OrderResponse> getOrderByOrderID(@PathVariable Long orderID) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderByOrderID(orderID))
                .build();
    }

    @GetMapping("/getOrder/{email}")
    public ApiResponse<List<OrderResponse>> getOrderByUser(@PathVariable String email){
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrderByUser(email))
                .build();
    }

    @PutMapping("/cancelOrder/{orderID}")
    public ApiResponse<String> cancelOrder(@PathVariable("orderID") Long orderID){
        return ApiResponse.<String>builder()
                .result(orderService.cancelOrder(orderID))
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrder(){
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrder())
                .build();
    }

    @PutMapping("/updateStatus/{orderID}")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable("orderID") Long orderID, @RequestBody UpdateOrderStatusRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderID, request))
                .build();
    }
    @PutMapping("/confirmOrder/{orderID}")
    public ApiResponse<String> confirmOrder(@PathVariable("orderID") Long orderID) {
        return ApiResponse.<String>builder()
                .result(orderService.confirmOrder(orderID))
                .build();
    }

    @PutMapping("/updateOrder/{orderID}")
    public ApiResponse<OrderResponse> updateOrder(@PathVariable("orderID") Long orderID ,@RequestBody @Valid OrderUpdateRequest request){
        logger.info("Received order request: {}", request);
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrder(request))
                .build();
    }
}
