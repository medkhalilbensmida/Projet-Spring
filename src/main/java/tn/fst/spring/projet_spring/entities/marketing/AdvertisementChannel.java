package tn.fst.spring.projet_spring.entities.marketing;
    
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.fst.spring.projet_spring.entities.marketing.config.GoogleAdsConfig;
import tn.fst.spring.projet_spring.entities.marketing.config.FacebookAdsConfig;

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
    
    // Platform-specific configs
    @OneToOne(cascade = CascadeType.ALL)
    private GoogleAdsConfig googleAdsConfig;

    @OneToOne(cascade = CascadeType.ALL)
    private FacebookAdsConfig facebookAdsConfig;
    
    @OneToMany(mappedBy = "channel")
    private Set<Advertisement> publicites;

    public AdvertisementChannel(ChannelType type, String plateforme, double coutMoyenParVue) {
        this.type = type;
        this.plateforme = plateforme;
        this.coutMoyenParVue = coutMoyenParVue;
    }

    @AssertTrue(message = "La configuration doit correspondre au type de canal")
    public boolean isValidConfig() {
        return (type == ChannelType.GOOGLE_ADS && googleAdsConfig != null && facebookAdsConfig == null)
            || (type == ChannelType.FACEBOOK && facebookAdsConfig != null && googleAdsConfig == null);
    }
}
