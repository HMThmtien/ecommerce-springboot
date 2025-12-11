package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductRatingSummary;
import com.example.ecommerce.dto.ReviewRequest;
import com.example.ecommerce.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long productId, ReviewRequest request);

    ReviewResponse updateReview(Long reviewId, ReviewRequest request);

    void deleteReview(Long reviewId);

    List<ReviewResponse> getReviewsByProduct(Long productId);

    ProductRatingSummary getRatingSummary(Long productId);
}
