package com.example.ecommerce.repository;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    @Modifying
    void deleteByUser(User user);
}
