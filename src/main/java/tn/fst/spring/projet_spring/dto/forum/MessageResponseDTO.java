package tn.fst.spring.projet_spring.dto.forum;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponseDTO {

    private Long id;
    private String message;
    private String senderUsername;
    private Long recipientId;
    private LocalDateTime timestamp;
}
