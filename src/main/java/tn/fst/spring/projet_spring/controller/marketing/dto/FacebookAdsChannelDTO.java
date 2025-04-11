package tn.fst.spring.projet_spring.controller.marketing.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FacebookAdsChannelDTO extends AdvertisementChannelDTO {

    private String facebookPageId;
    private String facebookAccessToken;
    private String compaignName;


    public FacebookAdsChannelDTO(Long id, String type, String platform, double averageCostPerView,
                               String facebookPageId, String facebookAccessToken,
                               String compaignName) {
        super(id, type, platform, averageCostPerView);
        this.facebookPageId = facebookPageId;
        this.facebookAccessToken = facebookAccessToken;
        this.compaignName = compaignName;

    }
}

