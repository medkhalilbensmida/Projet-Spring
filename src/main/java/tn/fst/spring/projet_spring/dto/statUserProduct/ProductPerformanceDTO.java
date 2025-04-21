package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

// ProductPerformanceDTO.java
@Data
public class ProductPerformanceDTO {
    private List<ProductSales> topSellingProducts;
    private List<ProductSales> worstSellingProducts;
    private Map<String, Double> abcAnalysis;
    private Map<String, Double> stockTurnover;
    private Map<String, Double> priceElasticity;

    @Data
    @AllArgsConstructor
    public static class ProductSales {
        private String name;
        private long quantitySold;
        private double unitPrice;
    }
}

