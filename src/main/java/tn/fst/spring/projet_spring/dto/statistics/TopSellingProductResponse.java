package tn.fst.spring.projet_spring.dto.statistics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TopSellingProductResponse {
    private List<Long> productIds;
    private List<String> productNames;
    private List<String> categories;
    private List<Integer> unitsSold;
    private List<Double> revenue;
}