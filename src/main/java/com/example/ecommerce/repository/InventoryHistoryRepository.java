package com.example.ecommerce.repository;

import com.example.ecommerce.entity.InventoryHistory;
import com.example.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {

    List<InventoryHistory> findByProductOrderByCreatedAtDesc(Product product);
}
