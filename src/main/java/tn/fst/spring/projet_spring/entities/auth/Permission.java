package tn.fst.spring.projet_spring.entities.auth;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}