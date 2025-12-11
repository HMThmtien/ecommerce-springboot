package com.example.ecommerce.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ReviewResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;

    private Long productId;
    private Integer rating;
    private String comment;

    private Instant createdAt;
    private Instant updatedAt;
}
