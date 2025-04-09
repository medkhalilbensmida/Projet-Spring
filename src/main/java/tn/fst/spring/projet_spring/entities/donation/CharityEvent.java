package tn.fst.spring.projet_spring.entities.donation;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class CharityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    private String description;

    @ManyToOne
    @JoinColumn(name = "fundraiser_id")
    private Fundraiser fundraiser;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<Donation> donations = new HashSet<>();
}