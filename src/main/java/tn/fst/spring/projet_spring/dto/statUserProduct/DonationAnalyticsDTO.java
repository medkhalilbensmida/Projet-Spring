package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class DonationAnalyticsDTO {
    private Map<String, Long> donationTrendLast30Days;
    private Map<String, Long> donationTrendLast90Days;
    private List<TopDonor> topDonors;
    private List<MostDonatedProduct> mostDonatedProducts;
    private Map<String, Double> seasonalAnalysis;

    @Data @AllArgsConstructor
    public static class TopDonor {
        private String username;
        private long totalQuantity;
    }

    @Data @AllArgsConstructor
    public static class MostDonatedProduct {
        private String name;
        private long quantity;
        private String category;
    }
}