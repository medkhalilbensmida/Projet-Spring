package tn.fst.spring.projet_spring.dto.donation;
import lombok.Data;

@Data
public class FundraiserRequestDTO {
    private String title;
    private String description;
    private double targetAmount;
}