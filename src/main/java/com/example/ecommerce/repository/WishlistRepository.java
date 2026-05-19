package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserOrderByCreatedAtDesc(User user);

    Optional<Wishlist> findByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    void deleteByUserId(Long userId);
}
