package com.example.TastyKing.repository;

import com.example.TastyKing.entity.Food;
import com.example.TastyKing.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {



    List<Review> findAllByFood(Food food);
}
