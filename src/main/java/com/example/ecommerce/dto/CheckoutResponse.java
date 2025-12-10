package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class CheckoutResponse {
    private Long orderId;
    private String message;
}
