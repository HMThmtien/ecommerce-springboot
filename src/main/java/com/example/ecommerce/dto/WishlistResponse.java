package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class WishlistResponse {

    private Long productId;
    private String name;
    private String imageUrl;
    private String description;
    private String categoryName;
    private String price;
}
