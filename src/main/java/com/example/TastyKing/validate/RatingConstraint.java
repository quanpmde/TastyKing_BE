package com.example.TastyKing.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RatingValidation.class)
public @interface RatingConstraint {
    String message() default "Rating must be greater than 1";



    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
