package tn.fst.spring.projet_spring.model.donation;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.entities.auth.User;
import tn.fst.spring.projet_spring.entities.catalog.Product;

@Data
@Entity
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private CharityEvent event;
}
