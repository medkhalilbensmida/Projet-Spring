package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.fst.spring.projet_spring.model.logistics.ComplaintStatus;

@Data
public class ComplaintUpdateStatusDTO {
    @NotNull(message = "New status cannot be null")
    private ComplaintStatus status;
}