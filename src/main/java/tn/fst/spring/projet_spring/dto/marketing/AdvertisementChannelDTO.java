package tn.fst.spring.projet_spring.dto.marketing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"  // This is the key that Jackson uses to determine the subclass
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GoogleAdsChannelDTO.class, name = "GOOGLE_ADS"),
    @JsonSubTypes.Type(value = FacebookAdsChannelDTO.class, name = "FACEBOOK")
    // Add other types if necessary
})
public class AdvertisementChannelDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("platform")
    private String platform;
    @JsonProperty("averageCostPerView")
    private double averageCostPerView;
}
