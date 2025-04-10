package tn.fst.spring.projet_spring.dto.products;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String barcode;
    private String description;
    private double price;
    private String category;
    private int stockQuantity;
}