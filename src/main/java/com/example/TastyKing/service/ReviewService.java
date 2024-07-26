package com.example.TastyKing.service;

import com.example.TastyKing.dto.request.ReviewRequest;
import com.example.TastyKing.dto.response.ReviewResponse;
import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.Review;
import com.example.TastyKing.entity.Tables;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.repository.FoodRepository;
import com.example.TastyKing.repository.OrderRepository;
import com.example.TastyKing.repository.ReviewRepository;
import com.example.TastyKing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    private OrderRepository orderRepository;

    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        User user = userRepository.findByEmail(reviewRequest.getUser().getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        Food food = foodRepository.findById(reviewRequest.getFood().getFoodID())
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));

        // Check if the user has any orders with status "Done"
        boolean hasDoneOrder = orderRepository.existsByUserAndOrderStatus(user, "Done");
        if (!hasDoneOrder) {
            throw new AppException(ErrorCode.ORDER_NOT_DONE);
        }

        Review review = Review.builder()
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
                .foodName(saveReview.getFood().getFoodName())
                .reviewText(saveReview.getReviewText())
                .rating(saveReview.getRating())
                .reviewDate(saveReview.getReviewDate())
                .build();
    }



    public List<ReviewResponse> getAllReview(){
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream().map(review -> ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .user(review.getUser())
                .foodName(review.getFood().getFoodName())
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .reviewDate(review.getReviewDate())
                .build()).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String deleteReview(Integer reviewID){
       reviewRepository.deleteById(reviewID);
       return "Delete review successfull";
    }
    public ReviewResponse getReviewByID(Integer reviewId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXIST));
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .user(review.getUser())
                .foodName(review.getFood().getFoodName())
                .reviewText(review.getReviewText())
                .reviewDate(review.getReviewDate())
                .rating(review.getRating())
                .build();
    }
}
