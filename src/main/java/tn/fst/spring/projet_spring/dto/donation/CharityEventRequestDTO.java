package tn.fst.spring.projet_spring.dto.donation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CharityEventRequestDTO {
    private String name;
    private String location;
    private LocalDateTime eventDate;
    private String description;
    private Long fundraiserId;
}
