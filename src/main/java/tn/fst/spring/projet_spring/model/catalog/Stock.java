package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//to avoid stack overflow error when using toString or equalsAndHashCode
@ToString(exclude = {"product"})
@EqualsAndHashCode(exclude = {"product"})
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