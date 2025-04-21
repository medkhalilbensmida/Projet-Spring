package tn.fst.spring.projet_spring.dto.forum;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private int likes;
    private int dislikes;
    private LocalDateTime createdAt;
}
