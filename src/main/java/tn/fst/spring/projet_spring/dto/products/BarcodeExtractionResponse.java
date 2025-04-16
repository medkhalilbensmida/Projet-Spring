package tn.fst.spring.projet_spring.dto.products;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BarcodeExtractionResponse {
    private String barcode;
    private boolean isTunisian;
}
