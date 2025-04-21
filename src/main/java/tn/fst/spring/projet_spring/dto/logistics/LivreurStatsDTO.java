package tn.fst.spring.projet_spring.dto.logistics;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LivreurStatsDTO {
    private Long livreurId;
    private String nom;
    private double assignedPercent;
    private double inTransitPercent;
    private double deliveredPercent;
    private double failedPercent;
    private long deliveredThisMonth;
}
