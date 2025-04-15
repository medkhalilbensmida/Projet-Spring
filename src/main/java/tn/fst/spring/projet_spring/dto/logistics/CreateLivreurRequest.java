package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLivreurRequest {

    @NotBlank(message = "Livreur name cannot be blank")
    private String nom;

    @NotNull(message = "Availability status is required")
    private Boolean disponible;
} 