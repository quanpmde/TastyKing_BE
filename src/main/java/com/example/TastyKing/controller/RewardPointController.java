package com.example.TastyKing.controller;

import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.RewardPointResponse;
import com.example.TastyKing.service.RewardPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RewardPointController {
    @Autowired
    private RewardPointService rewardPointService;

    @GetMapping("/{email}")
    public ApiResponse<RewardPointResponse> getRewardPoint(@PathVariable("email") String email){
        return ApiResponse.<RewardPointResponse>builder()
                .result(rewardPointService.getBalance(email))
                .build();
    }
}
