package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLivreurAvailabilityRequest {
    @NotNull(message = "Availability status cannot be null")
    private Boolean disponible;
} 