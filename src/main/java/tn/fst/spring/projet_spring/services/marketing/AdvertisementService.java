    package tn.fst.spring.projet_spring.services.marketing;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import tn.fst.spring.projet_spring.entities.marketing.Advertisement;
    import tn.fst.spring.projet_spring.entities.marketing.AdvertisementChannel;
    import tn.fst.spring.projet_spring.repositories.marketing.AdvertisementRepository;
    import java.util.List;
    import java.util.Optional;

    @Service
    @RequiredArgsConstructor
    public class AdvertisementService {

        private final AdvertisementRepository advertisementRepository;
        private final GoogleAdsService googleAdsService;

        public List<Advertisement> getAll() {
            return advertisementRepository.findAll();
        }

        public Optional<Advertisement> getById(Long id) {
            return advertisementRepository.findById(id);
        }

        public Advertisement save(Advertisement ad) {
            try {
                sendAdToChannel(ad);
                return advertisementRepository.save(ad);
            } catch (Exception e) {
                throw e;
            }

        }

        public void delete(Long id) {
            advertisementRepository.deleteById(id);
        }



        private void sendAdToChannel(Advertisement ad){
            AdvertisementChannel channel = ad.getChannel();
            switch (channel.getType()) {
                case GOOGLE_ADS:
                    // Call Google Ads API to create the ad
                    // Use channel.getGoogleCustomerId(), channel.getGoogleCampaignName(), etc.
                   googleAdsService.sendAdFromChannel(ad,channel);
                    break;
                case FACEBOOK:
                    // Call Facebook API to create the ad
                    break;
                case TWITTER:
                    // Call Instagram API to create the ad
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported advertisement channel: " + channel.getType());
            }



        }
    }
