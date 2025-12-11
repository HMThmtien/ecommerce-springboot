package com.example.ecommerce.controller;

import com.example.ecommerce.dto.WishlistResponse;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    public ApiResponse<Void> addToWishlist(@PathVariable Long productId) {
        wishlistService.addToWishlist(productId);
        return ApiResponse.ok("Đã thêm vào wishlist", null);
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(productId);
        return ApiResponse.ok("Đã xoá khỏi wishlist", null);
    }

    @GetMapping
    public ApiResponse<List<WishlistResponse>> getMyWishlist() {
        return ApiResponse.ok("Lấy wishlist thành công", wishlistService.getMyWishlist());
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearWishlist() {
        wishlistService.clearMyWishlist();
        return ApiResponse.ok("Đã xoá toàn bộ wishlist", null);
    }

    @GetMapping("/{productId}/exists")
    public ApiResponse<Boolean> checkExists(@PathVariable Long productId) {
        return ApiResponse.ok("Kiểm tra wishlist thành công", wishlistService.isInWishlist(productId));
    }
}
