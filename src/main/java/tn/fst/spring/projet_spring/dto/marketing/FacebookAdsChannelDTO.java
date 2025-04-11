    package tn.fst.spring.projet_spring.dto.marketing;

    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public class FacebookAdsChannelDTO extends AdvertisementChannelDTO {
        private Long id;
        private String type;
        private String platform;
        private double averageCostPerView;
        private String facebookPageId;
        private String facebookAccessToken;
        private String compaignName;


        public FacebookAdsChannelDTO(Long id, String type, String platform, double averageCostPerView,
                                String facebookPageId, String facebookAccessToken,
                                String compaignName) {

            super();
            this.id = id;
            this.type = type;
            this.platform = platform;
            this.averageCostPerView = averageCostPerView;
            this.facebookPageId = facebookPageId;
            this.facebookAccessToken = facebookAccessToken;
            this.compaignName = compaignName;

        }
    }

