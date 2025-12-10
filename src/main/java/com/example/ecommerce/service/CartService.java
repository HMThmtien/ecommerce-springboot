package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartItemDto;

import java.util.List;

public interface CartService {

    List<CartItemDto> getMyCart();

    void addToCart(AddToCartRequest request);

    void updateQuantity(Long cartItemId, int quantity);

    void removeItem(Long cartItemId);

    void clearMyCart();
}
