package tn.fst.spring.projet_spring.entities.auth;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import tn.fst.spring.projet_spring.entities.donation.Donation;
import tn.fst.spring.projet_spring.entities.forum.Comment;
import tn.fst.spring.projet_spring.entities.forum.ForumTopic;
import tn.fst.spring.projet_spring.entities.forum.Message;
import tn.fst.spring.projet_spring.entities.logistics.Complaint;
import tn.fst.spring.projet_spring.entities.order.Order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<ForumTopic> forumTopics = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private Set<Message> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private Set<Message> receivedMessages = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Complaint> complaints = new HashSet<>();

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private Set<Donation> donations = new HashSet<>();
}
