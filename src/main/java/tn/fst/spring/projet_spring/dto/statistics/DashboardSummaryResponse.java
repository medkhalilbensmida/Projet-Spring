package tn.fst.spring.projet_spring.dto.statistics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DashboardSummaryResponse {
    private double totalRevenue;
    private int totalOrders;
    private int pendingOrders;
    private int completedOrders;
    private int cancelledOrders;
    private double averageOrderValue;
    private double onlineSalesPercentage;
    private double doorToDoorSalesPercentage;
    private LocalDateTime lastUpdated;
}