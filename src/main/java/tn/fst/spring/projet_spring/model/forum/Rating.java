package tn.fst.spring.projet_spring.model.forum;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.model.auth.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "topic_rating",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"})
)

public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private ForumTopic topic;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
