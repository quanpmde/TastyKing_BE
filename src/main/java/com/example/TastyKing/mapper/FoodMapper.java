package com.example.TastyKing.mapper;

import com.example.TastyKing.dto.request.FoodRequest;
import com.example.TastyKing.dto.request.VoucherRequest;
import com.example.TastyKing.dto.response.FoodResponse;
import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface FoodMapper {

    FoodResponse toFoodResponse(Food food);
    @Mapping(source = "foodImage", target = "foodImage", qualifiedByName = "mapImage")
    Food toFood(FoodRequest request);
    @Named("mapImage")
    default String mapImage(MultipartFile foodImage) {
        if (foodImage != null && !foodImage.isEmpty()) {
            return foodImage.getOriginalFilename();
        }
        return null;
    }
}
