package com.example.TastyKing.service;

import com.example.TastyKing.dto.response.FoodResponse;
import com.example.TastyKing.entity.Food;
import com.example.TastyKing.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public List<FoodResponse> getTopOrderDetail() {
        List<Object[]> result = orderDetailRepository.findTopOrderedFoods();
        return result.stream()
                .limit(5)
                .map(objects -> {
                    Food food = (Food) objects[0];
                    return FoodResponse.builder()
                            .foodID(food.getFoodID())
                            .category(food.getCategory())
                            .foodName(food.getFoodName())
                            .foodPrice(food.getFoodPrice())

                            .description(food.getDescription())
                            .unit(food.getUnit())
                            .foodImage(food.getFoodImage())
                            .foodImage2(food.getFoodImage2())
                            .foodImage3(food.getFoodImage3())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
