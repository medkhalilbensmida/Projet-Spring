package tn.fst.spring.projet_spring.dto.forum;

import java.time.LocalDateTime;

public class CommentResponseDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private int likes;
    private LocalDateTime createdAt;
}
