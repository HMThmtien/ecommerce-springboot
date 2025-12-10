package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartItemDto;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private CartItemDto mapToDto(CartItem ci) {
        CartItemDto dto = new CartItemDto();
        dto.setId(ci.getId());
        dto.setProductId(ci.getProduct().getId());
        dto.setProductName(ci.getProduct().getName());
        dto.setQuantity(ci.getQuantity());
        dto.setStock(ci.getProduct().getStock());
        return dto;
    }

    @Override
    public List<CartItemDto> getMyCart() {
        User user = getCurrentUser();
        return cartItemRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void addToCart(AddToCartRequest request) {
        User user = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (request.getQuantity() > product.getStock()) {
            throw new BadRequestException("Số lượng vượt quá tồn kho");
        }

        CartItem existing = cartItemRepository.findByUserAndProduct(user, product)
                .orElse(null);

        if (existing == null) {
            CartItem item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(item);
        } else {
            int newQty = existing.getQuantity() + request.getQuantity();
            if (newQty > product.getStock()) {
                throw new BadRequestException("Số lượng vượt quá tồn kho");
            }
            existing.setQuantity(newQty);
            cartItemRepository.save(existing);
        }
    }

    @Override
    public void updateQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Số lượng phải > 0");
        }
        User user = getCurrentUser();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Không thể sửa cart của user khác");
        }
        if (quantity > item.getProduct().getStock()) {
            throw new BadRequestException("Số lượng vượt quá tồn kho");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    public void removeItem(Long cartItemId) {
        User user = getCurrentUser();
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Không thể xóa cart của user khác");
        }
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearMyCart() {
        User user = getCurrentUser();
        cartItemRepository.deleteByUser(user);
    }
}
