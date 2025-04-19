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

    // Weight is optional on update, but must be valid if provided
    @PositiveOrZero(message = "Product weight must be zero or positive")
    private Double weight; // Add weight (optional on update)

    @NotBlank
    private String categoryName;

    @PositiveOrZero
    private int stockQuantity;

    @PositiveOrZero
    private int minThreshold;
}
