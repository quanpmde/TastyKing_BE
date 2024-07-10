package com.example.TastyKing.service;


import com.example.TastyKing.dto.request.FoodRequest;
import com.example.TastyKing.dto.request.UpdateFoodRequest;
import com.example.TastyKing.dto.response.FoodResponse;
import com.example.TastyKing.dto.response.ReviewResponse;
import com.example.TastyKing.entity.Category;
import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.Review;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.mapper.FoodMapper;
import com.example.TastyKing.repository.CategoryRepository;
import com.example.TastyKing.repository.FoodRepository;

import com.example.TastyKing.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {
    private static final String UPLOAD_DIR = "D:\\FPT U\\FU-SU24\\SWP391\\TastyKing-FE\\img\\";
        @Autowired
        private final FoodRepository foodRepository;
        @Autowired
        private final FoodMapper foodMapper;
        @Autowired
        private final CategoryRepository categoryRepository;
        @Autowired
        private final ReviewRepository reviewRepository;


    @PreAuthorize("hasRole('ADMIN')")
        public FoodResponse addFood(FoodRequest foodRequest) throws IOException {
        String relativeImagePath = saveImage(foodRequest.getFoodImage());
            Food food = foodMapper.toFood(foodRequest);
            Category category = categoryRepository.findById(foodRequest.getCategoryID())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));
            food.setCategory(category);
            food.setFoodImage(relativeImagePath);
            Food savedFood = foodRepository.save(food);
            return foodMapper.toFoodResponse(savedFood);
        }

    private String saveImage(MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String imagePath = UPLOAD_DIR + image.getOriginalFilename();
            Files.write(Paths.get(imagePath), image.getBytes());
            return "img/" + image.getOriginalFilename();
        }
        return null;
    }

        public List<FoodResponse> getAllFood() {
            List<Food> foods = foodRepository.findAll();
            return foods.stream()
                    .map(foodMapper::toFoodResponse)
                    .collect(Collectors.toList());
        }

        public List<FoodResponse> getFoodByCategory(Long categoryId) {
            List<Food> foods = foodRepository.findByCategory_CategoryID(categoryId);
            return foods.stream()
                    .map(foodMapper::toFoodResponse)
                    .collect(Collectors.toList());
        }
    @PreAuthorize("hasRole('ADMIN')")
        public String deleteFood(Long foodID){
            foodRepository.deleteById(foodID);
            return "Deleted successfull";
        }

    @PreAuthorize("hasRole('ADMIN')")
        public FoodResponse updateFood(Long foodID, UpdateFoodRequest request) throws IOException{
        String relativeImagePath = saveImage(request.getFoodImage());
            Food food = foodRepository.findById(foodID).orElseThrow(() ->
                    new AppException(ErrorCode.FOOD_NOT_EXIST));
            Category category = categoryRepository.findById(request.getCategoryID())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));
            food.setCategory(category);
            food.setFoodName(request.getFoodName());
            food.setFoodCost(request.getFoodCost());
            food.setFoodImage(relativeImagePath);
            food.setDescription(request.getDescription());
            food.setFoodPrice(request.getFoodPrice());
            food.setUnit(request.getUnit());
           Food updateFood= foodRepository.save(food);
           return foodMapper.toFoodResponse(updateFood);
        }
        public FoodResponse getFoodByFoodID(Long foodID){
           Food food = foodRepository.findById(foodID).orElseThrow(() ->
                   new AppException(ErrorCode.FOOD_NOT_EXIST));
           return foodMapper.toFoodResponse(food);
        }

        public List<ReviewResponse> getAllReviewOfFoodItem(Long foodID){
            Food food = foodRepository.findById(foodID).orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_EXIST));
            List<Review> reviews = reviewRepository.findAllByFood(food);
            return reviews.stream()
                    .map(review -> ReviewResponse.builder()
                            .reviewId(review.getReviewId())
                            .user(review.getUser())
                            .food(review.getFood())
                            .reviewText(review.getReviewText())
                            .rating(review.getRating())
                            .reviewDate(review.getReviewDate())
                            .build())
                    .collect(Collectors.toList());
        }
    }