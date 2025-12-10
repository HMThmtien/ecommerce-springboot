package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevenueOverviewDto {

    private BigDecimal totalRevenue;      // Tổng doanh thu (mọi trạng thái hoặc chỉ PAID - tuỳ bạn)
    private long totalOrders;             // Tổng số đơn
    private long totalPaidOrders;         // Đơn đã thanh toán (status = PAID)
    private long totalPendingOrders;      // Đơn đang chờ (status = PENDING)
}
