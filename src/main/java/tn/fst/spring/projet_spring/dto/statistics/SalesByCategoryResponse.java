package tn.fst.spring.projet_spring.dto.statistics;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SalesByCategoryResponse {
    private List<String> categories;
    private List<Double> amounts;
    private List<Integer> counts;
    private Map<String, Double> percentageShare;
}