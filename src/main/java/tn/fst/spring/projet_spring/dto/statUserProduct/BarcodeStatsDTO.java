package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.Data;
import java.util.Map;

@Data
public class BarcodeStatsDTO {
    private int totalProductsWithBarcode;
    private int totalTunisianProducts;
    private double tunisianProductsPercentage;
    private Map<String, Long> productsByBarcodePrefix;
    private Map<String, Long> productsByBarcodeLength;
    private String mostCommonBarcodePrefix;
    private int mostCommonBarcodeLength;
}