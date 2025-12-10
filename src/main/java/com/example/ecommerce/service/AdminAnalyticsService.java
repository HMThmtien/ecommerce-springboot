package com.example.ecommerce.service;

import com.example.ecommerce.dto.DailyRevenueDto;
import com.example.ecommerce.dto.MonthlyRevenueDto;
import com.example.ecommerce.dto.RevenueOverviewDto;

import java.time.LocalDate;
import java.util.List;

public interface AdminAnalyticsService {

    RevenueOverviewDto getOverview();

    List<DailyRevenueDto> getDailyRevenue(LocalDate start, LocalDate end);

    List<MonthlyRevenueDto> getMonthlyRevenue(int year);
}
