package tn.fst.spring.projet_spring.dto.forum;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class MessageRequestDTO {
    @NotNull
    private Long requesterId;
    private Long senderId;
    @NotNull
    private String message;
}
