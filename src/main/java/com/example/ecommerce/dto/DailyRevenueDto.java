package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyRevenueDto {

    private LocalDate date;
    private BigDecimal revenue;
    private long orderCount;
}
