package com.example.TastyKing.controller;

import com.example.TastyKing.dto.request.RefundRequest;
import com.example.TastyKing.dto.request.UpdateRefundRequest;
import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.RefundResponse;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RefundController {
    @Autowired
    private RefundService refundService;

    @PostMapping
    public ResponseEntity<ApiResponse<RefundResponse>> createNewRefund(@ModelAttribute @RequestBody @Valid RefundRequest request) throws IOException {
        try {
            RefundResponse response = refundService.createNewRefund(request);
            return ResponseEntity.ok(ApiResponse.<RefundResponse>builder().result(response).build());
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.<RefundResponse>builder()
                            .code(e.getErrorCode().getCode())
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping
    public ApiResponse<List<RefundResponse>> getAllRefund(){
        return ApiResponse.<List<RefundResponse>>builder()
                .result(refundService.getAllRefund())
                .build();
    }

    @GetMapping("/get/{refundID}")
    public ApiResponse<RefundResponse> getRefundByRefundID(@PathVariable("refundID") Integer refundID) {
        return ApiResponse.<RefundResponse>builder()
                .result(refundService.getRefundByRefundID(refundID))
                .build();
    }
    @GetMapping("/get/order/{refundID}")
    public ApiResponse<RefundResponse> getRefundByOrderID(@PathVariable("refundID") Long orderID) {
        return ApiResponse.<RefundResponse>builder()
                .result(refundService.getRefundByOrderID(orderID))
                .build();
    }

    @PatchMapping("/update/order/{orderID}")
    public ApiResponse<RefundResponse> updateRefund(@PathVariable("orderID") Long orderID,@ModelAttribute @RequestBody @Valid UpdateRefundRequest request) throws IOException {
        return ApiResponse.<RefundResponse>builder()
                .result(refundService.updateRefund(orderID, request))
                .build();
    }
}