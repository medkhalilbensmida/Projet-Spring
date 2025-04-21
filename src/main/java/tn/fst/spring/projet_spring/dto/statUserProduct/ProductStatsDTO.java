package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.Data;
import java.util.Map;

@Data
public class ProductStatsDTO {
    private long totalProducts;
    private Map<String, Long> productsByCategory;
    private int productsBelowStockThreshold;
    private double avgPrice;
    private ProductInfo mostExpensiveProduct;
    private ProductInfo cheapestProduct;
    private ProductPriceDistribution priceDistribution;
    private CategoryStats mostPopularCategory;
    private CategoryStats leastPopularCategory;

    @Data
    public static class ProductInfo {
        private String name;
        private double price;
        private String category;
    }

    @Data
    public static class ProductPriceDistribution {
        private int under10;
        private int from10to50;
        private int from50to100;
        private int over100;
    }

    @Data
    public static class CategoryStats {
        private String name;
        private long productCount;
        private double avgPrice;
    }
}