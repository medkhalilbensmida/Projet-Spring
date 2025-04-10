package tn.fst.spring.projet_spring.entities.payment;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tn.fst.spring.projet_spring.entities.order.Order;

import java.time.LocalDateTime;

@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private boolean isPaid;

    public void markAsPaid() {
        this.isPaid = true;
    }
}
