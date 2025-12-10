package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.DailyRevenueDto;
import com.example.ecommerce.dto.MonthlyRevenueDto;
import com.example.ecommerce.dto.RevenueOverviewDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final OrderRepository orderRepository;

    @Override
    public RevenueOverviewDto getOverview() {
        List<Order> orders = orderRepository.findAll();

        RevenueOverviewDto dto = new RevenueOverviewDto();

        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = orders.size();
        long totalPaid = orders.stream().filter(o -> "PAID".equalsIgnoreCase(o.getStatus())).count();
        long totalPending = orders.stream().filter(o -> "PENDING".equalsIgnoreCase(o.getStatus())).count();

        dto.setTotalRevenue(totalRevenue);
        dto.setTotalOrders(totalOrders);
        dto.setTotalPaidOrders(totalPaid);
        dto.setTotalPendingOrders(totalPending);

        return dto;
    }

    @Override
    public List<DailyRevenueDto> getDailyRevenue(LocalDate start, LocalDate end) {
        // start, end dạng LocalDate -> convert sang Instant theo UTC
        Instant startInstant = start.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endInstant = end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Order> orders = orderRepository.findByCreatedAtBetween(startInstant, endInstant);

        Map<LocalDate, List<Order>> byDate = orders.stream()
                .collect(Collectors.groupingBy(o ->
                        LocalDateTime.ofInstant(o.getCreatedAt(), ZoneOffset.UTC).toLocalDate()
                ));

        List<DailyRevenueDto> result = new ArrayList<>();

        byDate.forEach((date, list) -> {
            BigDecimal revenue = list.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long count = list.size();

            DailyRevenueDto dto = new DailyRevenueDto();
            dto.setDate(date);
            dto.setRevenue(revenue);
            dto.setOrderCount(count);

            result.add(dto);
        });

        // sort theo ngày tăng dần
        result.sort(Comparator.comparing(DailyRevenueDto::getDate));
        return result;
    }

    @Override
    public List<MonthlyRevenueDto> getMonthlyRevenue(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1).minusDays(1);

        Instant startInstant = start.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endInstant = end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Order> orders = orderRepository.findByCreatedAtBetween(startInstant, endInstant);

        Map<Integer, List<Order>> byMonth = orders.stream()
                .collect(Collectors.groupingBy(o ->
                        LocalDateTime.ofInstant(o.getCreatedAt(), ZoneOffset.UTC).getMonthValue()
                ));

        List<MonthlyRevenueDto> result = new ArrayList<>();

        byMonth.forEach((month, list) -> {
            BigDecimal revenue = list.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long count = list.size();

            MonthlyRevenueDto dto = new MonthlyRevenueDto();
            dto.setYear(year);
            dto.setMonth(month);
            dto.setRevenue(revenue);
            dto.setOrderCount(count);

            result.add(dto);
        });

        result.sort(Comparator.comparingInt(MonthlyRevenueDto::getMonth));
        return result;
    }
}
