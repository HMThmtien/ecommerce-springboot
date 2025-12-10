package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyRevenueDto {

    private int year;
    private int month;        // 1–12
    private BigDecimal revenue;
    private long orderCount;
}
