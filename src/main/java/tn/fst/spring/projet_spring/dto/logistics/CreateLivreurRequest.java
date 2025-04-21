package tn.fst.spring.projet_spring.dto.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email; 
import lombok.Data;

@Data
public class CreateLivreurRequest {

    @NotBlank(message = "Nom cannot be blank")
    private String nom;

    @NotNull(message = "Disponibilité cannot be null")
    private Boolean disponible;

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid") 
    private String email;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;
}