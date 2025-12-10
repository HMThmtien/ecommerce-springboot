package com.example.ecommerce.controller;

import com.example.ecommerce.dto.DailyRevenueDto;
import com.example.ecommerce.dto.MonthlyRevenueDto;
import com.example.ecommerce.dto.RevenueOverviewDto;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;

    @GetMapping("/overview")
    public ApiResponse<RevenueOverviewDto> getOverview() {
        return ApiResponse.ok("Tổng quan doanh thu & đơn hàng", analyticsService.getOverview());
    }

    @GetMapping("/revenue/daily")
    public ApiResponse<List<DailyRevenueDto>> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ApiResponse.ok("Doanh thu theo ngày", analyticsService.getDailyRevenue(start, end));
    }

    @GetMapping("/revenue/monthly")
    public ApiResponse<List<MonthlyRevenueDto>> getMonthlyRevenue(
            @RequestParam int year
    ) {
        return ApiResponse.ok("Doanh thu theo tháng", analyticsService.getMonthlyRevenue(year));
    }
}
