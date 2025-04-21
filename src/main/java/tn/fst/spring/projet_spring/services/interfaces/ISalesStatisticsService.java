package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.statistics.*;

import java.time.LocalDateTime;

public interface ISalesStatisticsService {
    DashboardSummaryResponse getDashboardSummary(LocalDateTime startDate, LocalDateTime endDate);

    DailyRevenueResponse getDailyRevenue(LocalDateTime startDate, LocalDateTime endDate);

    SalesByCategoryResponse getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate);

    TopSellingProductResponse getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit);

    
    SalesByTypeResponse getSalesByType(LocalDateTime startDate, LocalDateTime endDate);
}