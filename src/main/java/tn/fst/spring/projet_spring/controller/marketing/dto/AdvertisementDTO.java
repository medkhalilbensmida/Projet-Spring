package tn.fst.spring.projet_spring.controller.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import tn.fst.spring.projet_spring.entities.marketing.Advertisement.AdvertisementType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AdvertisementDTO {

    private Long id;
    private String name;
    private Long channelId;
    private Long targetedAudienceId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double cost;
    private AdvertisementType type;
    private int views;
    private int initialViews;
}
