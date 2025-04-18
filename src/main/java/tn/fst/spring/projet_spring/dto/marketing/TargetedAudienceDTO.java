package tn.fst.spring.projet_spring.dto.marketing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TargetedAudienceDTO {

    private Long id;
    private String name;
    private Integer ageMin;
    private Integer ageMax;
    private String location;
    private String gender;
}
