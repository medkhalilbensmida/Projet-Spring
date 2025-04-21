package tn.fst.spring.projet_spring.dto.donation;

import lombok.Data;

@Data
public class DonationResponseDTO {
    private Long id;
    private String productName;
    private int quantity;
    private String donorFullName;
    private String eventName; // peut être null
}