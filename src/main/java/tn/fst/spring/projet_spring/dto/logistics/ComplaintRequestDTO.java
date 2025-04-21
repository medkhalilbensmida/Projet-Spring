package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintRequestDTO {

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotBlank(message = "Description cannot be blank")
    private String description;
}
