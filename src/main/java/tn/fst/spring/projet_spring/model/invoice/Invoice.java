package tn.fst.spring.projet_spring.model.invoice;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.model.order.Order;

import java.time.LocalDateTime;

@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private boolean isPaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType type;

    private String billingAddress;

    private String shippingAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime dueDate;

    private String taxId;

    private double taxAmount;

    @Column(nullable = false)
    private boolean isActive = true;

    public void markAsPaid() {
        this.isPaid = true;
    }
}