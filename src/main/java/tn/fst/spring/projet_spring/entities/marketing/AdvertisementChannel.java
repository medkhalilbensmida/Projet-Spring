package tn.fst.spring.projet_spring.entities.marketing;
    
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
@Data
@NoArgsConstructor
@Entity
public class AdvertisementChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type; // Ex: Facebook, Google Ads, etc.

    private String plateforme;

    private double coutMoyenParVue;

    @OneToMany(mappedBy = "channel")
    private Set<Advertisement> publicites;
}
