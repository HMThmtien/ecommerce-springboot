package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sản phẩm liên quan
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Số lượng thay đổi (dương: nhập thêm, âm: xuất/bán ra)
    @Column(nullable = false)
    private Integer quantityChange;

    // Loại thay đổi: IMPORT, SALE, ADJUST, RETURN,...
    @Column(nullable = false)
    private String type;

    // Ghi chú
    private String note;

    // Thời điểm
    @Column(nullable = false)
    private Instant createdAt;
}
