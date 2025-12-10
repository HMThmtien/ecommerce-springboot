package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartItemDto;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<List<CartItemDto>> getMyCart() {
        return ApiResponse.ok("Lấy giỏ hàng thành công", cartService.getMyCart());
    }

    @PostMapping("/add")
    public ApiResponse<Void> addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(request);
        return ApiResponse.ok("Thêm vào giỏ hàng thành công", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateQuantity(@PathVariable Long id,
                                            @RequestParam int quantity) {
        cartService.updateQuantity(id, quantity);
        return ApiResponse.ok("Cập nhật số lượng thành công", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return ApiResponse.ok("Xóa item khỏi giỏ thành công", null);
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart() {
        cartService.clearMyCart();
        return ApiResponse.ok("Xóa toàn bộ giỏ hàng thành công", null);
    }
}
