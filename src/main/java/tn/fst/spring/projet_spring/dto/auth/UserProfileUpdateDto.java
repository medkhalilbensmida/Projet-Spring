package tn.fst.spring.projet_spring.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileUpdateDto {

    private String username;

    @Email(message = "Veuillez saisir un email valide (ex : nom@domaine.com)")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial"
    )
    private String password;
}
