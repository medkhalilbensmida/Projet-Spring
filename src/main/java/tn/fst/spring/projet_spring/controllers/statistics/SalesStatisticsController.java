package tn.fst.spring.projet_spring.controllers.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.statistics.*;
import tn.fst.spring.projet_spring.services.interfaces.ISalesStatisticsService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Sales Statistics", description = "Dashboard analytics and sales statistics endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class SalesStatisticsController {

    private final ISalesStatisticsService salesStatisticsService;

    @GetMapping("/dashboard")
    @Operation(
            summary = "Get dashboard summary statistics",
            description = "Returns key metrics including total revenue, order counts, and sales distribution"
    )
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @Parameter(description = "Start date (default: 30 days ago)", example = "2025-04-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date (default: current date)", example = "2025-04-21T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        return ResponseEntity.ok(salesStatisticsService.getDashboardSummary(startDate, endDate));
    }

    @GetMapping("/daily-revenue")
    @Operation(
            summary = "Get daily revenue data",
            description = "Returns revenue and order count trends by day"
    )
    public ResponseEntity<DailyRevenueResponse> getDailyRevenue(
            @Parameter(description = "Start date (default: 30 days ago)", example = "2025-04-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date (default: current date)", example = "2025-04-21T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Group by (daily/weekly/monthly)", schema = @Schema(allowableValues = {"daily", "weekly", "monthly"}))
            @RequestParam(required = false, defaultValue = "daily") String groupBy) {

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        return ResponseEntity.ok(salesStatisticsService.getDailyRevenue(startDate, endDate));
    }

    @GetMapping("/sales-by-category")
    @Operation(
            summary = "Get sales by product category",
            description = "Returns sales metrics and distribution across product categories"
    )
    public ResponseEntity<SalesByCategoryResponse> getSalesByCategory(
            @Parameter(description = "Start date (default: 30 days ago)", example = "2025-04-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date (default: current date)", example = "2025-04-21T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Sort by (revenue/quantity)", schema = @Schema(allowableValues = {"revenue", "quantity"}))
            @RequestParam(required = false, defaultValue = "revenue") String sortBy,

            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        return ResponseEntity.ok(salesStatisticsService.getSalesByCategory(startDate, endDate));
    }

    @GetMapping("/top-selling-products")
    @Operation(
            summary = "Get top selling products",
            description = "Returns best performing products ranked by quantity sold or revenue"
    )
    public ResponseEntity<TopSellingProductResponse> getTopSellingProducts(
            @Parameter(description = "Start date (default: 30 days ago)", example = "2025-04-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date (default: current date)", example = "2025-04-21T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Number of products to return", example = "10")
            @RequestParam(required = false, defaultValue = "10") int limit,

            @Parameter(description = "Sort by (revenue/quantity)", schema = @Schema(allowableValues = {"revenue", "quantity"}))
            @RequestParam(required = false, defaultValue = "quantity") String sortBy) {

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        return ResponseEntity.ok(salesStatisticsService.getTopSellingProducts(startDate, endDate, limit));
    }

    @GetMapping("/sales-by-type")
    @Operation(
            summary = "Get sales by sale type",
            description = "Returns comparison metrics between online and door-to-door sales"
    )
    public ResponseEntity<SalesByTypeResponse> getSalesByType(
            @Parameter(description = "Start date (default: 30 days ago)", example = "2025-04-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date (default: current date)", example = "2025-04-21T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Include order details", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean includeDetails) {

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30).truncatedTo(ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        return ResponseEntity.ok(salesStatisticsService.getSalesByType(startDate, endDate));
    }
}