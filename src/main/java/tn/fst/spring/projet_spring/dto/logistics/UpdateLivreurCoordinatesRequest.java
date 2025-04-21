package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLivreurCoordinatesRequest {

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;
} 