package com.example.TastyKing.controller;

import com.example.TastyKing.dto.request.VoucherExchangeRequest;
import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.VoucherExchangeResponse;
import com.example.TastyKing.service.VoucherExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voucherExchange")
@RequiredArgsConstructor
@CrossOrigin("*")   
public class VoucherExchangeController {
    @Autowired
    private VoucherExchangeService voucherExchangeService;

    @PostMapping
    public ApiResponse<VoucherExchangeResponse> createVoucherExchange(@RequestBody VoucherExchangeRequest request){
        return ApiResponse.<VoucherExchangeResponse>builder()
                .result(voucherExchangeService.createVoucherExchange(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<VoucherExchangeResponse>> getAllVoucherExchange()
    {
        return ApiResponse.<List<VoucherExchangeResponse>>builder()
                .result(voucherExchangeService.getAllVoucherExchange())
                .build();
    }

    @GetMapping("/{voucherExchangeID}")
    public ApiResponse<VoucherExchangeResponse> getVoucherExchangeByID(@PathVariable("voucherExchangeID") Long voucherExchangeID){
        return ApiResponse.<VoucherExchangeResponse>builder()
                .result(voucherExchangeService.getVoucherExchangeByID(voucherExchangeID))
                .build();
    }

    @GetMapping("/getVoucherExchange/{email}")
    public ApiResponse<List<VoucherExchangeResponse>> getVoucherExchangeByEmail(@PathVariable("email") String email){
        return ApiResponse.<List<VoucherExchangeResponse>>builder()
                .result(voucherExchangeService.getVoucherExchangeByEmail(email))
                .build();
    }
}
