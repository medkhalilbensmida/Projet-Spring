package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email; 
import lombok.Data;

@Data
public class UpdateLivreurRequest {

    // If present and not blank, update the name.
    // If null, keep the existing name.
    @NotBlank(message = "Livreur name cannot be blank if provided") // Allows null
    private String nom;

    // If present (not null), update availability.
    // If null, keep existing availability.
    private Boolean disponible;
    
    private String phoneNumber;
    @Email(message = "Email should be valid") 
    private String email;
    // private String location;

    private Double latitude;
    private Double longitude;
} 