package com.example.TastyKing.dto.response;

import com.example.TastyKing.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RewardPointResponse {
    private int rewardID;
    private User user;
    private Double balance;
}
