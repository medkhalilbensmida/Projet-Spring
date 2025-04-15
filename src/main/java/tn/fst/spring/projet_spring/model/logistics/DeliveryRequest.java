package tn.fst.spring.projet_spring.model.logistics;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.model.order.Order;

@Data
@Entity
public class DeliveryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;

    @Column(nullable = false)
    private double deliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    public void assignLivreur(Livreur livreur) {
        this.livreur = livreur;
        this.status = DeliveryStatus.ASSIGNED;
    }

    public void calculateDeliveryFee() {
        // Implementation of delivery fee calculation logic
        this.deliveryFee = 5.0; // Base fee + additional logic
    }
}