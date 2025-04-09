package tn.fst.spring.projet_spring.entities.donation;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Fundraiser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private double targetAmount;

    private double collectedAmount;

    @OneToMany(mappedBy = "fundraiser", cascade = CascadeType.ALL)
    private Set<CharityEvent> events = new HashSet<>();

    public void addContribution(double amount) {
        this.collectedAmount += amount;
    }
}