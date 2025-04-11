package tn.fst.spring.projet_spring.services.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.entities.marketing.AdvertisementChannel;
import tn.fst.spring.projet_spring.repositories.marketing.AdvertisementChannelRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementChannelService {

    private final GoogleAdsService googleAdsService;
    private final AdvertisementChannelRepository repository;

    public List<AdvertisementChannel> getAll() {
        return repository.findAll();
    }

    public Optional<AdvertisementChannel> getById(Long id) {
        return repository.findById(id);
    }

    public AdvertisementChannel save(AdvertisementChannel channel) {
        try{
            AdvertisementChannel result = this.configureCampaign(channel);
            if (result == null){
                throw new IllegalArgumentException("Failed to configure advertisement channel");
            }
            return repository.save(channel);
        }
        catch (Exception e){
            throw e;
        }
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }


    private AdvertisementChannel configureCampaign(AdvertisementChannel channel){
        // Configure the advertisement campaign based on the channel type
        switch (channel.getType()) {
            case GOOGLE_ADS:
                // Call Google Ads API to create the ad
                // Use channel.getGoogleCustomerId(), channel.getGoogleCampaignName(), etc.
                Map<String, String> resources = googleAdsService.createChannelCampaign(channel);
                if (resources == null || resources.isEmpty()) {
                    throw new IllegalArgumentException("Failed to create Google Ads campaign");
                }
                channel.getGoogleAdsConfig().setCampaignResourceName(resources.get("campaignResourceName"));
                channel.getGoogleAdsConfig().setAdResourceName(resources.get("adResourceName"));
                return channel;
            case FACEBOOK:
                // Call Facebook API to create the ad
                break;
            default:
                throw new IllegalArgumentException("Unsupported advertisement channel: " + channel.getType());
        }
        return null;
    }
}
