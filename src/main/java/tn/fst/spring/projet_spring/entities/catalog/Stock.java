package tn.fst.spring.projet_spring.entities.catalog;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int minThreshold;

    public boolean checkAvailability(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }

    public void updateQuantity(int delta) {
        this.quantity += delta;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }
}