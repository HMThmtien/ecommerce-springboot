package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class CartItemDto {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer stock;
}
