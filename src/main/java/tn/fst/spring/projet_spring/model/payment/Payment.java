package tn.fst.spring.projet_spring.model.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import tn.fst.spring.projet_spring.model.order.Order;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"order"})
@EqualsAndHashCode(exclude = {"order"})
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    private String cardLastFourDigits;
    
    private String paymentProviderReference;
    
    private boolean successful;
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    public void processPayment() {
        this.paymentDate = LocalDateTime.now();
        this.successful = true;
    }
    
    /**
     * Validates that payment amount matches the order's total amount
     * @return true if amounts match within a small delta (0.01)
     */
    public boolean validateAmountMatchesOrder() {
        return order != null && Math.abs(this.amount - order.getTotalAmount()) <= 0.01;
    }
}