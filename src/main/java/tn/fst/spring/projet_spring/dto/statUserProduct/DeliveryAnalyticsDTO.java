package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.Data;
import java.util.Map;

@Data
public class DeliveryAnalyticsDTO {
    private double deliverySuccessRate;
    private Map<String, Double> livreurPerformance;
    private Map<String, Long> lateDeliveriesAnalysis;
}
