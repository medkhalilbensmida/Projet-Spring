// src/main/java/tn/fst/spring/projet_spring/controllers/statUserProduct/AdvancedStatsController.java
package tn.fst.spring.projet_spring.controllers.statUserProduct;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.statUserProduct.*;
import tn.fst.spring.projet_spring.services.interfaces.IAdvancedStatsService;

@RestController
@RequestMapping("/api/advanced-stats")
@RequiredArgsConstructor
public class AdvancedStatsController {
    private final IAdvancedStatsService advancedStatsService;

    @GetMapping("/user-behavior")
    public ResponseEntity<UserBehaviorStatsDTO> getUserBehaviorStats() {
        return ResponseEntity.ok(advancedStatsService.getUserBehaviorStats());
    }

    @GetMapping("/product-performance")
    public ResponseEntity<ProductPerformanceDTO> getProductPerformanceStats() {
        return ResponseEntity.ok(advancedStatsService.getProductPerformanceStats());
    }

    @GetMapping("/donation-analytics")
    public ResponseEntity<DonationAnalyticsDTO> getDonationAnalytics() {
        return ResponseEntity.ok(advancedStatsService.getDonationAnalytics());
    }

    @GetMapping("/delivery-analytics")
    public ResponseEntity<DeliveryAnalyticsDTO> getDeliveryAnalytics() {
        return ResponseEntity.ok(advancedStatsService.getDeliveryAnalytics());
    }
}
