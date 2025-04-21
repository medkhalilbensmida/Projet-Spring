package tn.fst.spring.projet_spring.model.marketing;

import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@Entity
@Schema(description = "Representation of a targeted audience for marketing campaigns")
public class TargetedAudience {
    
    @Schema(description = "Unique identifier of the targeted audience", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    @Schema(description = "Name of the targeted audience group", example = "Young Adults Tunisia")
    private String nom; // Exemple : "Jeunes adultes Tunisie"
    
    @Schema(description = "Minimum age of the audience group", example = "18")
    private Integer ageMin;

    @Schema(description = "Maximum age of the audience group", example = "30")
    private Integer ageMax;

    @Schema(description = "Location of the targeted audience group", example = "Tunis")
    private String localisation; // Exemple : Tunis, Sousse, etc.

    @Schema(description = "Gender of the targeted audience group", example = "Mixed")
    private String genre; // "Homme", "Femme", "Tous"

    @OneToMany(mappedBy = "targetedAudience")
    private Set<Advertisement> advertisements;
}
