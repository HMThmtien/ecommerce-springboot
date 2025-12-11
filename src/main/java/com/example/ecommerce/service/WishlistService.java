package com.example.ecommerce.service;

import com.example.ecommerce.dto.WishlistResponse;

import java.util.List;

public interface WishlistService {

    void addToWishlist(Long productId);

    void removeFromWishlist(Long productId);

    List<WishlistResponse> getMyWishlist();

    void clearMyWishlist();

    boolean isInWishlist(Long productId);
}
