package tn.fst.spring.projet_spring.dto.forum;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ForumTopicResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private double rating;
    private LocalDateTime createdAt;
//    private List<CommentResponseDTO> comments; // Optionnel, selon besoin
}
