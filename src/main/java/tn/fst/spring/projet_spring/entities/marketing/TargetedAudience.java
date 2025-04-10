package tn.fst.spring.projet_spring.entities.marketing;

import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class TargetedAudience {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ageMin;

    private Integer ageMax;

    private String localisation; // Exemple : Tunis, Sousse, etc.

    private String genre; // "Homme", "Femme", "Tous"

    @OneToMany(mappedBy = "targetedAudience")
    private Set<Advertisement> advertisements;
}
