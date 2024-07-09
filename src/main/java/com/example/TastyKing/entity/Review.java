package com.example.TastyKing.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FoodID", nullable = false)
    private Food food;

    @Column(name = "ReviewText", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "Rating")
    private int rating;

    @Column(name = "ReviewDate")
    private LocalDateTime reviewDate;

    // Constructors, getters, and setters
}
