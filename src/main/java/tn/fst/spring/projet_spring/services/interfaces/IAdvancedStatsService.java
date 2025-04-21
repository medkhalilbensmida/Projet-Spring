// src/main/java/tn/fst/spring/projet_spring/services/interfaces/IAdvancedStatsService.java
package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.statUserProduct.*;

public interface IAdvancedStatsService {
    UserBehaviorStatsDTO getUserBehaviorStats();
    ProductPerformanceDTO getProductPerformanceStats();
    DonationAnalyticsDTO getDonationAnalytics();
    DeliveryAnalyticsDTO getDeliveryAnalytics();
}
