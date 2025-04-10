package tn.fst.spring.projet_spring.controller.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TargetedAudienceDTO {

    private Long id;
    private Integer ageMin;
    private Integer ageMax;
    private String location;
    private String gender;
}
