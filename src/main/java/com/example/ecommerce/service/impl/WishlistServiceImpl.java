package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.WishlistResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.WishlistRepository;
import com.example.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public void addToWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));

        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new BadRequestException("Sản phẩm đã nằm trong wishlist");
        }

        Wishlist w = Wishlist.builder()
                .user(user)
                .product(product)
                .createdAt(Instant.now())
                .build();

        wishlistRepository.save(w);
    }

    @Override
    public void removeFromWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));

        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    @Override
    public List<WishlistResponse> getMyWishlist() {
        User user = getCurrentUser();

        return wishlistRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(w -> {
                    WishlistResponse res = new WishlistResponse();
                    res.setProductId(w.getProduct().getId());
                    res.setName(w.getProduct().getName());
                    res.setImageUrl(w.getProduct().getImageUrl());
                    res.setDescription(w.getProduct().getDescription());
                    res.setCategoryName(w.getProduct().getCategory() != null ?
                            w.getProduct().getCategory().getName() : null);
                    res.setPrice(w.getProduct().getPrice().toPlainString());
                    return res;
                })
                .toList();
    }

    @Override
    public void clearMyWishlist() {
        User user = getCurrentUser();
        wishlistRepository.deleteByUser(user);
    }

    @Override
    public boolean isInWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));
        return wishlistRepository.existsByUserAndProduct(user, product);
    }
}
