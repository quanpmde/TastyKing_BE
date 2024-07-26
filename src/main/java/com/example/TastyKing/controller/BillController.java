package com.example.TastyKing.controller;

import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.service.BillService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BillController {

    @Autowired
    private final BillService billService;
    @GetMapping("/print/{orderID}")
    public void printBill(@PathVariable Long orderID, HttpServletResponse response) {
        try {
            billService.generatePdfBill(orderID, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
