package tn.fst.spring.projet_spring.dto.products;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String barcode;

    private String description;

    @Positive
    private double price;

    @NotNull(message = "Product weight must not be null, default is 0")
    @PositiveOrZero(message = "Product weight must be zero or positive")
    private Double weight = 0.0;

    @NotBlank
    private String categoryName;

    @PositiveOrZero
    private int initialQuantity;

    @PositiveOrZero
    private int minThreshold;
}