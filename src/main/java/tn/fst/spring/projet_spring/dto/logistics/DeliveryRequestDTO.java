package tn.fst.spring.projet_spring.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;

@Data
@AllArgsConstructor
public class DeliveryRequestDTO {
    private Long id;
    private double deliveryFee;
    private DeliveryStatus status;
    private Long orderId;
}
