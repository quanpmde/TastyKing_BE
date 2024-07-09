package com.example.TastyKing.service;

import com.example.TastyKing.dto.request.ReviewRequest;
import com.example.TastyKing.dto.response.ReviewResponse;
import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.Review;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.repository.FoodRepository;
import com.example.TastyKing.repository.ReviewRepository;
import com.example.TastyKing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;

    public ReviewResponse createReview(ReviewRequest reviewRequest){
        User user = userRepository.findByEmail(reviewRequest.getUser().getEmail()).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        Food food = foodRepository.findById(reviewRequest.getFood().getFoodID()).orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));

        Review review;
        review =Review.builder()
                .user(user)
                .food(food)
                .reviewText(reviewRequest.getReviewText())
                .rating(reviewRequest.getRating())
                .reviewDate(LocalDateTime.now())
                .build();

        Review saveReview = reviewRepository.save(review);
        return ReviewResponse.builder()
                .reviewId(saveReview.getReviewId())
                .user(saveReview.getUser())
                .food(saveReview.getFood())
                .reviewText(saveReview.getReviewText())
                .rating(saveReview.getRating())
                .reviewDate(saveReview.getReviewDate())
                .build();
    }




    public String deleteReview(Integer reviewID){
       reviewRepository.deleteById(reviewID);
       return "Delete review successfull";
    }
}
