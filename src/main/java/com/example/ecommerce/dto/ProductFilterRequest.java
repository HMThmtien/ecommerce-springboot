package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilterRequest {

    private String keyword;       // tìm theo tên / mô tả
    private Long categoryId;      // filter theo category
    private BigDecimal minPrice;  // giá tối thiểu
    private BigDecimal maxPrice;  // giá tối đa

    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "desc"; // asc / desc
}
