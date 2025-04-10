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
    

    // Google Ads specific fields
    private String googleCustomerId  = null;  // Google Ads Customer ID
    private String googleCampaignName = null;  // Campaign Name
    private String googleAdGroupName = null;  // Ad Group Name
    private Long googleCompaignBudget = null;
    private String googleAdResourceName = null;  // Ad Resource Name
    private String googleCampaignResourceName = null;  // Campaign Resource Name


    @OneToMany(mappedBy = "channel")
    private Set<Advertisement> publicites;
}
