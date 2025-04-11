@Configuration
public class MarketingTestInitializer {

    @Bean
    public ApplicationRunner testMarketingRepositories(
            AdvertisementChannelRepository channelRepo,
            AdvertisementRepository adRepo,
            TargetedAudienceRepository audienceRepo
    ) {
        return args -> {
            // 🧹 Suppression des anciennes données
            adRepo.deleteAll();
            channelRepo.deleteAll();
            audienceRepo.deleteAll();

            // 🎯 Step 1: Créer une audience ciblée
            TargetedAudience audience = new TargetedAudience();
            audience.setNom("Jeunes adultes Tunisie");
            audience.setAgeMax(30);
            audience.setAgeMin(18);
            audience.setGenre("Mixte");
            audience.setLocalisation("Tunis");

            audienceRepo.save(audience);

            // 📡 Step 2: Créer un canal publicitaire
            AdvertisementChannel channel = new AdvertisementChannel();
            channel.setPlateforme("Google");
            channel.setType(ChannelType.GOOGLE_ADS);
            channel.setCoutMoyenParVue(0.15);

            channelRepo.save(channel);

            // 📢 Step 3: Créer une publicité liée au canal et à l’audience
            Advertisement ad = new Advertisement();
            ad.setName("Promo Ramadan");
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
            System.out.println("🎉 Publicité enregistrée : " + adRepo.findAll());
        };
    }
}
