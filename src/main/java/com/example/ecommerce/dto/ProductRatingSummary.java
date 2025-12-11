package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class ProductRatingSummary {

    private Long productId;
    private double averageRating;
    private long totalReviews;
}
