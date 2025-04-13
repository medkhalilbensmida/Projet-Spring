package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ProductPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    private int x; // position dans le rayon (colonne)
    private int y; // position dans le rayon (ligne)

    private int width;  // taille que prend le produit sur le rayon
    private int height;

    private int zIndex; // ordre d’affichage si superposition (optionnel)
}
