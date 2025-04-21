package tn.fst.spring.projet_spring.dto.logistics;

import tn.fst.spring.projet_spring.model.logistics.ResolutionType;
import tn.fst.spring.projet_spring.model.logistics.ResolutionStatus;
import lombok.Data;

@Data
@lombok.Builder
public class ResolutionResponseDTO {
    private Long id;
    private ResolutionType type;
    private String description;
    private ResolutionStatus status;
    private Long complaintId;
}
