package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserBehaviorStatsDTO {
    private Map<String, Double> avgSessionsPerUser;

    private Map<String, String> avgSessionDuration;

    private Map<String, Double> userSegmentation;

    private Map<String, Double> retentionRates;

    private Map<String, Map<String, Double>> cohortAnalysis;
}