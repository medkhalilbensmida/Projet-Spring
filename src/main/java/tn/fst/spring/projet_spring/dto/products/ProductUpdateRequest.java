package tn.fst.spring.projet_spring.dto.products;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductUpdateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String barcode;

    private String description;

    @Positive
    private double price;

    @NotBlank
    private String categoryName;

    @PositiveOrZero
    private int stockQuantity;

    @PositiveOrZero
    private int minThreshold;
}
