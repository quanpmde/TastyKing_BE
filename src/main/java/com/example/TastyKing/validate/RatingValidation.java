package com.example.TastyKing.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RatingValidation implements ConstraintValidator<RatingConstraint, Integer> {


    @Override
    public void initialize(RatingConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer rating, ConstraintValidatorContext constraintValidatorContext) {
        if (rating == null) {
            return false; // Rating cannot be null
        }
        return rating > 1;
    }
}
