package tn.fst.spring.projet_spring.model.marketing;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TargetAudience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String criteria;

    @OneToOne
    @JoinColumn(name = "advertisement_id", nullable = false)
    private Advertisement advertisement;
}