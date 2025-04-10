package tn.fst.spring.projet_spring.controller.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdvertisementChannelDTO {

    private Long id;
    private String type;
    private String platform;
    private double averageCostPerView;
}
