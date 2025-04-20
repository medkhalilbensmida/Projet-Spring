package tn.fst.spring.projet_spring.model.forum;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.model.auth.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private ForumTopic topic;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private int likes;
    private int dislikes;
    @ManyToMany
    @JoinTable(
            name = "comment_liked_by",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "comment_disliked_by",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> dislikedBy = new HashSet<>();


    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void moderate() {
        // Implementation of moderation logic
    }
}