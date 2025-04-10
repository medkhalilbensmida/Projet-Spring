package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type;

    @ManyToMany(mappedBy = "shelves")
    private Set<Product> products = new HashSet<>();

    // Constructeur pratique
    public Shelf(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Shelf() {}
}