package com.example.TastyKing.service;

import com.example.TastyKing.dto.response.RewardPointResponse;
import com.example.TastyKing.entity.RewardPoint;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.repository.RewardPointRepository;
import com.example.TastyKing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardPointService {
    @Autowired
    private RewardPointRepository rewardPointRepository;
    @Autowired
    private UserRepository userRepository;

    public RewardPointResponse getBalance(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        RewardPoint rewardPoint = rewardPointRepository.findByUser(user).orElseThrow(() -> new AppException(ErrorCode.REWARD_NO_EXIST));

        return RewardPointResponse.builder()
                .rewardID(rewardPoint.getRewardID())
                .user(rewardPoint.getUser())
                .balance(rewardPoint.getBalance())
                .build();
    }
}
