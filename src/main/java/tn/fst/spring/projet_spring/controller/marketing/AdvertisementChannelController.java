package tn.fst.spring.projet_spring.controller.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.controller.marketing.dto.AdvertisementChannelDTO;
import tn.fst.spring.projet_spring.controller.marketing.dto.FacebookAdsChannelDTO;
import tn.fst.spring.projet_spring.controller.marketing.dto.GoogleAdsChannelDTO;
import tn.fst.spring.projet_spring.entities.marketing.AdvertisementChannel;
import tn.fst.spring.projet_spring.entities.marketing.ChannelType;
import tn.fst.spring.projet_spring.entities.marketing.config.FacebookAdsConfig;
import tn.fst.spring.projet_spring.entities.marketing.config.GoogleAdsConfig;
import tn.fst.spring.projet_spring.services.marketing.AdvertisementChannelService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class AdvertisementChannelController {

    private final AdvertisementChannelService service;

    @GetMapping
    public List<AdvertisementChannelDTO> getAll() {
        return service.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementChannelDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(channel -> ResponseEntity.ok(toDTO(channel)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdvertisementChannelDTO> create(@RequestBody AdvertisementChannelDTO channelDTO) {
        try {
            AdvertisementChannel channel = toEntity(channelDTO);
            AdvertisementChannel savedChannel = service.save(channel);
            return ResponseEntity.ok(toDTO(savedChannel));  // Return the DTO of the saved channel
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid channel type: " + channelDTO.getType(), e); 
        } catch (Exception e) {
            throw new RuntimeException("Failed to create advertisement channel", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementChannelDTO> update(@PathVariable Long id, @RequestBody AdvertisementChannelDTO updatedDTO) {
        return service.getById(id)
                .map(existing -> {
                    AdvertisementChannel updated = toEntity(updatedDTO);
                    updated.setId(id);
                    return ResponseEntity.ok(toDTO(service.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Convert AdvertisementChannel entity to AdvertisementChannelDTO
    private AdvertisementChannelDTO toDTO(AdvertisementChannel channel) {

        if (channel == null) {
            return null;
        }

    switch (channel.getType()){
        case GOOGLE_ADS:
            return new GoogleAdsChannelDTO(
                channel.getId(),
                channel.getType().name(),  // ChannelType to String (Enum)
                channel.getPlateforme(),
                channel.getCoutMoyenParVue(),
                channel.getGoogleAdsConfig().getCustomerId(),
                channel.getGoogleAdsConfig().getCampaignName(),
                channel.getGoogleAdsConfig().getAdGroupName(),
                channel.getGoogleAdsConfig().getCampaignBudgetMicros(),
                channel.getGoogleAdsConfig().getAdResourceName(),
                channel.getGoogleAdsConfig().getCampaignResourceName()
        );            
        case FACEBOOK:
            return new FacebookAdsChannelDTO(
                channel.getId(),
                channel.getType().name(),  // ChannelType to String (Enum)
                channel.getPlateforme(),
                channel.getCoutMoyenParVue(),
                channel.getFacebookAdsConfig().getPageId(),
                channel.getFacebookAdsConfig().getAccessToken(),
                channel.getFacebookAdsConfig().getCampaignName()
            );
        case TWITTER:
                // Handle Twitter Ads DTO conversion
                break;
            default:
                throw new IllegalArgumentException("Unknown channel type: " + channel.getType());
        }
        return null;


    }

   // Convert AdvertisementChannelDTO to AdvertisementChannel entity
private AdvertisementChannel toEntity(AdvertisementChannelDTO dto) {
    AdvertisementChannel channel = new AdvertisementChannel();

 
    // Check for the specific DTO types
    if (dto instanceof GoogleAdsChannelDTO googleAdsDTO) {
        GoogleAdsConfig googleAdsConfig = new GoogleAdsConfig();
        channel.setId(dto.getId());
        channel.setType(ChannelType.valueOf("GOOGLE_ADS"));  // Convert String to Enum
        channel.setPlateforme(dto.getPlatform());
        channel.setCoutMoyenParVue(dto.getAverageCostPerView());
        googleAdsConfig.setCustomerId(googleAdsDTO.getGoogleCustomerId());
        googleAdsConfig.setCampaignName(googleAdsDTO.getGoogleCampaignName());
        googleAdsConfig.setAdGroupName(googleAdsDTO.getGoogleAdGroupName());
        googleAdsConfig.setCampaignBudgetMicros(googleAdsDTO.getGoogleCompaignBudget());
        googleAdsConfig.setAdResourceName(googleAdsDTO.getGoogleAdResourceName());
        googleAdsConfig.setCampaignResourceName(googleAdsDTO.getGoogleCampaignResourceName());
        channel.setGoogleAdsConfig(googleAdsConfig);
    } else if (dto instanceof FacebookAdsChannelDTO facebookAdsDTO) {
        FacebookAdsConfig facebookAdsConfig = new FacebookAdsConfig();
        channel.setId(dto.getId());
        channel.setType(ChannelType.valueOf(dto.getType()));  // Convert String to Enum
        channel.setPlateforme(dto.getPlatform());
        channel.setCoutMoyenParVue(dto.getAverageCostPerView());
        facebookAdsConfig.setPageId(facebookAdsDTO.getFacebookPageId());
        facebookAdsConfig.setAccessToken(facebookAdsDTO.getFacebookAccessToken());
        facebookAdsConfig.setCampaignName(facebookAdsDTO.getCompaignName());
        channel.setFacebookAdsConfig(facebookAdsConfig);
    } else {
        // Throw exception for unknown DTO types
        throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass().getName());
    }

    return channel;
}

}
