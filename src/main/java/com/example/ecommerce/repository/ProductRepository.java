package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByCategory(Category category, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    List<Product> findByStockLessThanEqual(int threshold);

    @Query("SELECT p FROM Product p")
    Page<Product> findAllProducts(Pageable pageable);


    @Query("""
    SELECT p FROM Product p
    LEFT JOIN p.category c
    WHERE (:keyword IS NULL 
           OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:categoryId IS NULL OR c.id = :categoryId)
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
""")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

}
