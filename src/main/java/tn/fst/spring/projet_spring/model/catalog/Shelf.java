package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type;
    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL)
    private Set<ProductPosition> positions = new HashSet<>();
    
    // Constructeur pratique
    public Shelf(String name, String type) {
        this.name = name;
        this.type = type;
    }
    @Column
    private int x; // position sur la carte (ligne)

    @Column
    private int y; // position sur la carte (colonne)

    @Column
    private int width; // largeur du rayon
    @Column
    private int height; // hauteur du rayon

    public Shelf() {}
}