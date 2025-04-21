package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.statistics.*;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.order.SaleType;
import tn.fst.spring.projet_spring.repositories.statistics.SalesStatisticsRepository;
import tn.fst.spring.projet_spring.security.SecurityUtil;
import tn.fst.spring.projet_spring.services.interfaces.ISalesStatisticsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesStatisticsServiceImpl implements ISalesStatisticsService {

    private final SalesStatisticsRepository salesStatisticsRepository;
    private final SecurityUtil securityUtil;

    @Override
    public DashboardSummaryResponse getDashboardSummary(LocalDateTime startDate, LocalDateTime endDate) {
        // Check permissions - only admin can access
        if (!securityUtil.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can access sales statistics");
        }

        // Get total orders and revenue
        long totalOrders = salesStatisticsRepository.countOrdersInPeriod(startDate, endDate);
        Double totalRevenue = salesStatisticsRepository.calculateRevenueInPeriod(startDate, endDate);
        if (totalRevenue == null) totalRevenue = 0.0;

        // Get orders by status
        long pendingOrders = salesStatisticsRepository.countOrdersByStatusInPeriod(OrderStatus.PENDING, startDate, endDate);
        long confirmedOrders = salesStatisticsRepository.countOrdersByStatusInPeriod(OrderStatus.CONFIRMED, startDate, endDate);
        long processingOrders = salesStatisticsRepository.countOrdersByStatusInPeriod(OrderStatus.PROCESSING, startDate, endDate);
        long shippedOrders = salesStatisticsRepository.countOrdersByStatusInPeriod(OrderStatus.SHIPPED, startDate, endDate);
        long deliveredOrders = salesStatisticsRepository.countOrdersByStatusInPeriod(OrderStatus.DELIVERED, startDate, endDate);
        long cancelledOrders = salesStatisticsRepository.countOrdersByStatusInPeriod(OrderStatus.CANCELLED, startDate, endDate);

        // Calculate completed orders (DELIVERED)
        long completedOrders = deliveredOrders;

        // Calculate average order value
        double averageOrderValue = salesStatisticsRepository.calculateAverageOrderValueInPeriod(startDate, endDate);

        // Get sales by type
        long onlineOrders = salesStatisticsRepository.countOrdersBySaleTypeInPeriod(SaleType.ONLINE, startDate, endDate);
        long doorToDoorOrders = salesStatisticsRepository.countOrdersBySaleTypeInPeriod(SaleType.DOOR_TO_DOOR, startDate, endDate);

        // Calculate percentages
        double onlinePercentage = totalOrders > 0 ? (onlineOrders * 100.0 / totalOrders) : 0;
        double doorToDoorPercentage = totalOrders > 0 ? (doorToDoorOrders * 100.0 / totalOrders) : 0;

        return DashboardSummaryResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders((int) totalOrders)
                .pendingOrders((int) pendingOrders)
                .completedOrders((int) completedOrders)
                .cancelledOrders((int) cancelledOrders)
                .averageOrderValue(averageOrderValue)
                .onlineSalesPercentage(onlinePercentage)
                .doorToDoorSalesPercentage(doorToDoorPercentage)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Override
    public DailyRevenueResponse getDailyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        // Check permissions - only admin can access
        if (!securityUtil.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can access sales statistics");
        }

        List<Map<String, Object>> dailyRevenueData = salesStatisticsRepository.getDailyRevenue(startDate, endDate);

        List<LocalDate> dates = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        List<Integer> orderCounts = new ArrayList<>();

        for (Map<String, Object> data : dailyRevenueData) {
            dates.add(((java.sql.Date) data.get("date")).toLocalDate());
            revenues.add(((Number) data.get("revenue")).doubleValue());
            orderCounts.add(((Number) data.get("count")).intValue());
        }

        return DailyRevenueResponse.builder()
                .dates(dates)
                .revenues(revenues)
                .orderCounts(orderCounts)
                .build();
    }

    @Override
    public SalesByCategoryResponse getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        // Check permissions - only admin can access
        if (!securityUtil.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can access sales statistics");
        }

        List<Map<String, Object>> categoryData = salesStatisticsRepository.getSalesByCategory(startDate, endDate);

        List<String> categories = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Map<String, Double> percentageShare = new HashMap<>();

        double totalAmount = 0;
        for (Map<String, Object> data : categoryData) {
            totalAmount += ((Number) data.get("revenue")).doubleValue();
        }

        for (Map<String, Object> data : categoryData) {
            String category = (String) data.get("category");
            double amount = ((Number) data.get("revenue")).doubleValue();
            int count = ((Number) data.get("quantity")).intValue();

            categories.add(category);
            amounts.add(amount);
            counts.add(count);

            double percentage = totalAmount > 0 ? (amount * 100 / totalAmount) : 0;
            percentageShare.put(category, percentage);
        }

        return SalesByCategoryResponse.builder()
                .categories(categories)
                .amounts(amounts)
                .counts(counts)
                .percentageShare(percentageShare)
                .build();
    }

    @Override
    public TopSellingProductResponse getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        // Check permissions - only admin can access
        if (!securityUtil.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can access sales statistics");
        }

        if (limit <= 0) limit = 10; // Default to top 10 if not specified or invalid

        List<Map<String, Object>> productsData = salesStatisticsRepository.getTopSellingProducts(startDate, endDate, limit);

        List<Long> productIds = new ArrayList<>();
        List<String> productNames = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Integer> unitsSold = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();

        for (Map<String, Object> data : productsData) {
            productIds.add(((Number) data.get("productId")).longValue());
            productNames.add((String) data.get("productName"));
            categories.add((String) data.get("category"));
            unitsSold.add(((Number) data.get("quantity")).intValue());
            revenue.add(((Number) data.get("revenue")).doubleValue());
        }

        return TopSellingProductResponse.builder()
                .productIds(productIds)
                .productNames(productNames)
                .categories(categories)
                .unitsSold(unitsSold)
                .revenue(revenue)
                .build();
    }

    @Override
public SalesByTypeResponse getSalesByType(LocalDateTime startDate, LocalDateTime endDate) {
    // Check permissions - only admin can access
    if (!securityUtil.isAdmin()) {
        throw new org.springframework.security.access.AccessDeniedException("Only administrators can access sales statistics");
    }

    List<Map<String, Object>> salesTypeData = salesStatisticsRepository.getSalesByType(startDate, endDate);

    int totalOnlineOrders = 0;
    int totalDoorToDoorOrders = 0;
    double onlineRevenue = 0;
    double doorToDoorRevenue = 0;

    for (Map<String, Object> data : salesTypeData) {
        // Fix: Get the saleType as String and then convert to enum
        String saleTypeStr = data.get("saleType").toString();
        SaleType saleType = SaleType.valueOf(saleTypeStr);
        
        int count = ((Number) data.get("count")).intValue();
        double amount = ((Number) data.get("revenue")).doubleValue();

        if (saleType == SaleType.ONLINE) {
            totalOnlineOrders = count;
            onlineRevenue = amount;
        } else if (saleType == SaleType.DOOR_TO_DOOR) {
            totalDoorToDoorOrders = count;
            doorToDoorRevenue = amount;
        }
    }

    int totalOrders = totalOnlineOrders + totalDoorToDoorOrders;
    double totalRevenue = onlineRevenue + doorToDoorRevenue;

    double onlinePercentage = totalOrders > 0 ? (totalOnlineOrders * 100.0 / totalOrders) : 0;
    double doorToDoorPercentage = totalOrders > 0 ? (totalDoorToDoorOrders * 100.0 / totalOrders) : 0;

    return SalesByTypeResponse.builder()
            .totalOnlineOrders(totalOnlineOrders)
            .totalDoorToDoorOrders(totalDoorToDoorOrders)
            .onlineRevenue(onlineRevenue)
            .doorToDoorRevenue(doorToDoorRevenue)
            .onlinePercentage(onlinePercentage)
            .doorToDoorPercentage(doorToDoorPercentage)
            .build();
}
}
