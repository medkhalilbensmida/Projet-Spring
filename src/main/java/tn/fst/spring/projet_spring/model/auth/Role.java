package tn.fst.spring.projet_spring.model.auth;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Exemple: ROLE_ADMIN, ROLE_CUSTOMER

    private String description;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}