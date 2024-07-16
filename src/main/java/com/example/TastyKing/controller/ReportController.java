package com.example.TastyKing.controller;

import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin("*")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/daily")
    public ApiResponse<Map<String, Object>> getDailyReport(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(reportService.getDailyReport(date))
                .build();
    }

    @GetMapping("/monthly")
    public ApiResponse<Map<String, Object>> getMonthlyReport(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(reportService.getMonthlyReport(date))
                .build();
    }

    @GetMapping("/yearly")
    public ApiResponse<Map<String, Object>> getYearlyReport(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(reportService.getYearlyReport(date))
                .build();
    }
    @GetMapping("/monthlyRevenue")
    public ApiResponse<Map<String, Double>> getMonthlyTotalAmountForLast12Months() {
        return ApiResponse.<Map<String, Double>>builder()
                .result(reportService.getMonthlyRevenue())
                .build();
    }
}
