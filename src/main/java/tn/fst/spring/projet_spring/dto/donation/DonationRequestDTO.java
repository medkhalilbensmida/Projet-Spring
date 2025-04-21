package tn.fst.spring.projet_spring.dto.donation;

import lombok.Data;

@Data
public class DonationRequestDTO {
    private Long productId;
    private int quantity;
    private Long eventId; // peut être null
}