package tn.fst.spring.projet_spring.entities.payment;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tn.fst.spring.projet_spring.entities.order.Order;

import java.time.LocalDateTime;

@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public void processPayment() {
        this.paymentDate = LocalDateTime.now();
    }
}
