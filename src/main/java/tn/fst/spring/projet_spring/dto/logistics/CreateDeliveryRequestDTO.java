package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class CreateDeliveryRequestDTO {
    @NotNull
    private Long orderId;
    private Long livreurId;
    // Weight is now calculated from the order items
    // Origin coordinates are now fetched from config

    @NotNull(message = "Destination latitude must not be null")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double destinationLat; // Change to Double

    @NotNull(message = "Destination longitude must not be null")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double destinationLon; // Change to Double
}
