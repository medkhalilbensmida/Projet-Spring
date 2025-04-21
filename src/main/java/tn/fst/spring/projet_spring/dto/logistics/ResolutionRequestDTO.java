package tn.fst.spring.projet_spring.dto.logistics;

import tn.fst.spring.projet_spring.model.logistics.ResolutionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResolutionRequestDTO {
    @NotNull
    private ResolutionType type;
    @NotNull
    private String description;
}
