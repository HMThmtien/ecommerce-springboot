package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.ProductRatingSummary;
import com.example.ecommerce.dto.ReviewRequest;
import com.example.ecommerce.dto.ReviewResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse res = new ReviewResponse();
        res.setId(review.getId());
        res.setUserId(review.getUser().getId());
        res.setUserEmail(review.getUser().getEmail());
        res.setUserFullName(review.getUser().getFullName());
        res.setProductId(review.getProduct().getId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setCreatedAt(review.getCreatedAt());
        res.setUpdatedAt(review.getUpdatedAt());
        return res;
    }

    @Override
    public ReviewResponse createReview(Long productId, ReviewRequest request) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Chỉ user đã mua hàng mới được review (nếu không muốn, có thể bỏ check này)
        boolean hasBought = orderItemRepository.existsByOrder_UserAndProduct(user, product);
        if (!hasBought) {
            throw new BadRequestException("Bạn chỉ có thể đánh giá sản phẩm đã mua");
        }

        // Nếu đã review rồi -> cho phép update, hoặc báo lỗi tùy bạn
        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này rồi, hãy sửa review thay vì tạo mới");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        User user = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        // chỉ chủ review hoặc admin mới được sửa
        boolean isOwner = review.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("Bạn không có quyền sửa review này");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUpdatedAt(Instant.now());

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Override
    public void deleteReview(Long reviewId) {
        User user = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        boolean isOwner = review.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("Bạn không có quyền xoá review này");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return reviewRepository.findByProductOrderByCreatedAtDesc(product)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductRatingSummary getRatingSummary(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        List<Review> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);

        long total = reviews.size();
        double avg = 0.0;
        if (total > 0) {
            int sum = reviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();
            avg = (double) sum / total;
        }

        ProductRatingSummary summary = new ProductRatingSummary();
        summary.setProductId(productId);
        summary.setTotalReviews(total);
        summary.setAverageRating(avg);

        return summary;
    }
}
