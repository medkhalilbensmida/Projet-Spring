package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;

@Data
public class UpdateDeliveryStatusDTO {

    @NotNull(message = "New status cannot be null")
    private DeliveryStatus newStatus;
} 