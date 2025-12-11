package com.example.ecommerce.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class InventoryHistoryResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantityChange;
    private String type;      // IMPORT, SALE, ADJUST...
    private String note;
    private Instant createdAt;
}
