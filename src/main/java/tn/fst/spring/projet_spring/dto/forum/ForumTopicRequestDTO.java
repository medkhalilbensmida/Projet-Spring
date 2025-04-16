package tn.fst.spring.projet_spring.dto.forum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForumTopicRequestDTO {
    private String title;
    private String content;
    private Long authorId;
}
