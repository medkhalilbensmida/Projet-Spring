package tn.fst.spring.projet_spring.dto.donation;

import lombok.Data;

@Data
public class FundraiserResponseDTO {
    private Long id;
    private String title;
    private String description;
    private double targetAmount;
    private double collectedAmount;
}