package tn.fst.spring.projet_spring.dto.donation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CharityEventResponseDTO {
    private Long id;
    private String name;
    private String location;
    private LocalDateTime eventDate;
    private String description;
    private String fundraiserTitle;
}