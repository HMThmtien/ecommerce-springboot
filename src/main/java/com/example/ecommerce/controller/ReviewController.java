package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductRatingSummary;
import com.example.ecommerce.dto.ReviewRequest;
import com.example.ecommerce.dto.ReviewResponse;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // USER: tạo review cho product (đã login & đã mua hàng)
    @PostMapping("/api/products/{productId}/reviews")
    public ApiResponse<ReviewResponse> createReview(@PathVariable Long productId,
                                                    @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Tạo review thành công", reviewService.createReview(productId, request));
    }

    // USER: xem tất cả review của product (public)
    @GetMapping("/api/products/{productId}/reviews")
    public ApiResponse<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ApiResponse.ok("Lấy danh sách review thành công",
                reviewService.getReviewsByProduct(productId));
    }

    // USER: lấy summary rating của 1 product
    @GetMapping("/api/products/{productId}/rating")
    public ApiResponse<ProductRatingSummary> getRatingSummary(@PathVariable Long productId) {
        return ApiResponse.ok("Lấy thông tin rating thành công",
                reviewService.getRatingSummary(productId));
    }

    // USER/ADMIN: sửa review
    @PutMapping("/api/reviews/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(@PathVariable Long reviewId,
                                                    @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Cập nhật review thành công",
                reviewService.updateReview(reviewId, request));
    }

    // USER/ADMIN: xoá review
    @DeleteMapping("/api/reviews/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.ok("Xoá review thành công", null);
    }
}
