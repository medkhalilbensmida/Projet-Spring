package tn.fst.spring.projet_spring.model.marketing.config;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAdsConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private String customerId;
    private String campaignName;
    private String adGroupName;
    private Long campaignBudgetMicros;
    private String adResourceName;
    private String campaignResourceName;

}
