package tn.fst.spring.projet_spring.model.forum;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.entities.auth.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class ForumTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private int rating;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();
}