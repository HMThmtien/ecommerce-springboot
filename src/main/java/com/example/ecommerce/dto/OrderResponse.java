package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private BigDecimal totalAmount;
    private String status;
    private Instant createdAt;
    private String paymentStatus;
    private String paymentMethod;
    private Instant paidAt;

    private List<OrderItemResponse> items;
}
