package tn.fst.spring.projet_spring.config;

import java.time.LocalDate;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tn.fst.spring.projet_spring.model.marketing.Advertisement;
import tn.fst.spring.projet_spring.model.marketing.AdvertisementChannel;
import tn.fst.spring.projet_spring.model.marketing.ChannelType;
import tn.fst.spring.projet_spring.model.marketing.TargetedAudience;
import tn.fst.spring.projet_spring.repositories.marketing.AdvertisementChannelRepository;
import tn.fst.spring.projet_spring.repositories.marketing.AdvertisementRepository;
import tn.fst.spring.projet_spring.repositories.marketing.GoogleAdsConfigRepository;
import tn.fst.spring.projet_spring.repositories.marketing.TargetedAudienceRepository;
import tn.fst.spring.projet_spring.model.marketing.config.GoogleAdsConfig;
@Configuration
public class MarketingTestInitializer {

    @Bean
    public ApplicationRunner testMarketingRepositories(
            AdvertisementChannelRepository channelRepo,
            AdvertisementRepository adRepo,
            TargetedAudienceRepository audienceRepo,
            GoogleAdsConfigRepository googleAdsRepo
    ) {
        return args -> {
            // 🧹 Suppression des anciennes données
            adRepo.deleteAll();
            channelRepo.deleteAll();
            audienceRepo.deleteAll();
            googleAdsRepo.deleteAll();

            // Step 1: Créer une configuration spécifique pour le canal
            // (Exemple pour Google Ads)
            GoogleAdsConfig googleConfig = new GoogleAdsConfig();
            googleConfig.setCampaignBudgetMicros(Long.valueOf(1000L));
            googleConfig.setCampaignName("Promo Ramadan");
            googleConfig.setCustomerId("Consommi Tounsi");
            googleConfig.setAdGroupName("Ramadan");

            // Enregistrer la configuration dans le canal
            // GoogleAdsConfig config = googleAdsRepo.save(googleConfig);
            

            // 🎯 Step 2: Créer une audience ciblée
            TargetedAudience audience = new TargetedAudience();
            audience.setNom("Jeunes adultes Tunisie");
            audience.setAgeMax(30);
            audience.setAgeMin(18);
            audience.setGenre("Mixte");
            audience.setLocalisation("Tunis");

            audienceRepo.save(audience);

            // 📡 Step 3: Créer un canal publicitaire
            AdvertisementChannel channel = new AdvertisementChannel();
            channel.setPlateforme("Google");
            channel.setType(ChannelType.GOOGLE_ADS);
            channel.setCoutMoyenParVue(0.15);
            channel.setGoogleAdsConfig(googleConfig); // Associer la configuration Google Ads
            channelRepo.save(channel);



            // 📢 Step 4: Créer une publicité liée au canal et à l’audience
            Advertisement ad = new Advertisement();
            ad.setName("Promo Ramadan: Ne ratte pas cette Occasion");
            ad.setDescription("Réductions jusqu’à 50% !");
            ad.setStartDate(LocalDate.now());
            ad.setEndDate(LocalDate.now().plusDays(10));
            ad.setChannel(channel);
            ad.setTargetedAudience(audience);
            ad.setUrl("https://example.com/promo");
            ad.setCost(1000.0);
            ad.setType(Advertisement.AdvertisementType.TEXT);
            ad.setInitialViews(0);
            ad.setViews(500);

            adRepo.save(ad);

            // ✅ Test
            System.out.println("🎉 Publicité enregistrée : ");
        };
    }
}
