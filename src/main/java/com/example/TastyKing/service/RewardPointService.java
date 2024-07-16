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

import java.util.List;
import java.util.stream.Collectors;

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


    public List<RewardPointResponse> rewardPointRank() {
        return rewardPointRepository.findTop5ByOrderByBalanceDesc()
                .stream()
                .map(rp -> RewardPointResponse.builder()
                        .rewardID(rp.getRewardID())
                        .user(rp.getUser())
                        .balance(rp.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

}
