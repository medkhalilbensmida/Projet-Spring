package tn.fst.spring.projet_spring.controller.marketing;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.fst.spring.projet_spring.dto.marketing.AdvertisementChannelDTO;
import tn.fst.spring.projet_spring.dto.marketing.FacebookAdsChannelDTO;
import tn.fst.spring.projet_spring.dto.marketing.GoogleAdsChannelDTO;
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

    @Operation(summary = "Get all advertisement channels")
    @ApiResponse(responseCode = "200", description = "List of all advertisement channels",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdvertisementChannelDTO.class)))
    @GetMapping
    public List<AdvertisementChannelDTO> getAll() {
        return service.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get a channel by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Channel found", content = @Content(schema = @Schema(implementation = AdvertisementChannelDTO.class))),
        @ApiResponse(responseCode = "404", description = "Channel not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementChannelDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(channel -> ResponseEntity.ok(toDTO(channel)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new advertisement channel", description = "Create either a Google or Facebook advertisement channel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Channel created", content = @Content(schema = @Schema(implementation = AdvertisementChannelDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid channel type")
    })
    @PostMapping
    public ResponseEntity<AdvertisementChannelDTO> create(
        @RequestBody(
            description = "Channel DTO (Google or Facebook)",
            required = true,
            content = @Content(
                // schema = @Schema(oneOf = {GoogleAdsChannelDTO.class, FacebookAdsChannelDTO.class}),
                examples = {
                    @ExampleObject(name = "Google Ads Channel", summary = "Google Ads Channel Example", value = """
                    {
                      "type": "GOOGLE_ADS",
                      "platform": "Google",
                      "averageCostPerView": 0.12,
                      "googleCustomerId": "123-456-7890",
                      "googleCampaignName": "Spring Campaign",
                      "googleAdGroupName": "Promo Group",
                      "googleCompaignBudget": 10000000,
                      "googleAdResourceName": "customers/1234567890/ads/1",
                      "googleCampaignResourceName": "customers/1234567890/campaigns/1"
                    }
                    """),
                    @ExampleObject(name = "Facebook Ads Channel", summary = "Facebook Ads Channel Example", value = """
                    {
                      "type": "FACEBOOK",
                      "platform": "Facebook",
                      "averageCostPerView": 0.08,
                      "facebookPageId": "123456789",
                      "facebookAccessToken": "EAAGm0PX4ZCpsBA...",
                      "compaignName": "FB Spring Launch"
                    }
                    """)
                }
            )
        ) AdvertisementChannelDTO channelDTO) {
        try {
            AdvertisementChannel channel = toEntity(channelDTO);
            AdvertisementChannel savedChannel = service.save(channel);
            return ResponseEntity.ok(toDTO(savedChannel));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid channel type: " + channelDTO.getType(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create advertisement channel", e);
        }
    }

    @Operation(summary = "Update a channel by ID")
    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementChannelDTO> update(
        @PathVariable Long id,
        @RequestBody AdvertisementChannelDTO updatedDTO) {
        return service.getById(id)
                .map(existing -> {
                    AdvertisementChannel updated = toEntity(updatedDTO);
                    updated.setId(id);
                    return ResponseEntity.ok(toDTO(service.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a channel by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Channel deleted"),
        @ApiResponse(responseCode = "404", description = "Channel not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AdvertisementChannelDTO toDTO(AdvertisementChannel channel) {
        if (channel == null) {
            return null;
        }

        return switch (channel.getType()) {
            case GOOGLE_ADS -> new GoogleAdsChannelDTO(
                channel.getType().name(),
                channel.getPlateforme(),
                channel.getCoutMoyenParVue(),
                channel.getGoogleAdsConfig().getCustomerId(),
                channel.getGoogleAdsConfig().getCampaignName(),
                channel.getGoogleAdsConfig().getAdGroupName(),
                channel.getGoogleAdsConfig().getCampaignBudgetMicros(),
                channel.getGoogleAdsConfig().getAdResourceName(),
                channel.getGoogleAdsConfig().getCampaignResourceName()
            );
            case FACEBOOK -> new FacebookAdsChannelDTO(
                channel.getType().name(),
                channel.getPlateforme(),
                channel.getCoutMoyenParVue(),
                channel.getFacebookAdsConfig().getPageId(),
                channel.getFacebookAdsConfig().getAccessToken(),
                channel.getFacebookAdsConfig().getCampaignName()
            );
            case TWITTER -> throw new UnsupportedOperationException("Twitter channel conversion not implemented");
        };
    }

    private AdvertisementChannel toEntity(AdvertisementChannelDTO dto) {
        AdvertisementChannel channel = new AdvertisementChannel();

        System.out.println(dto);
        if (dto instanceof GoogleAdsChannelDTO googleAdsDTO) {
            GoogleAdsConfig googleAdsConfig = new GoogleAdsConfig();
            channel.setType(ChannelType.valueOf("GOOGLE_ADS"));
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
            channel.setType(ChannelType.valueOf(dto.getType()));
            channel.setPlateforme(dto.getPlatform());
            channel.setCoutMoyenParVue(dto.getAverageCostPerView());
            facebookAdsConfig.setPageId(facebookAdsDTO.getFacebookPageId());
            facebookAdsConfig.setAccessToken(facebookAdsDTO.getFacebookAccessToken());
            facebookAdsConfig.setCampaignName(facebookAdsDTO.getCompaignName());
            channel.setFacebookAdsConfig(facebookAdsConfig);
        } else {
            throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass().getName());
        }

        return channel;
    }
}
