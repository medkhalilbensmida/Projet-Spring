package tn.fst.spring.projet_spring.dto.marketing;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GoogleAdsChannelDTO extends AdvertisementChannelDTO {
    private Long id;
    private String type;
    private String platform;
    private double averageCostPerView;
    private String googleCustomerId;
    private String googleCampaignName;
    private String googleAdGroupName;
    private Long googleCompaignBudget;
    private String googleAdResourceName;
    private String googleCampaignResourceName;

    public GoogleAdsChannelDTO(Long id, String type, String platform, double averageCostPerView,
                               String googleCustomerId, String googleCampaignName,
                               String googleAdGroupName, Long googleCompaignBudget,
                               String googleAdResourceName, String googleCampaignResourceName) {
        super();
        this.id = id;
        this.type = type;
        this.platform = platform;
        this.averageCostPerView = averageCostPerView;
        this.googleCustomerId = googleCustomerId;
        this.googleCampaignName = googleCampaignName;
        this.googleAdGroupName = googleAdGroupName;
        this.googleCompaignBudget = googleCompaignBudget;
        this.googleAdResourceName = googleAdResourceName;
        this.googleCampaignResourceName = googleCampaignResourceName;
    }
}
