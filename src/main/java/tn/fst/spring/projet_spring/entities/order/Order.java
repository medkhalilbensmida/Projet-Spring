package tn.fst.spring.projet_spring.entities.order;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.entities.auth.User;
import tn.fst.spring.projet_spring.entities.logistics.Complaint;
import tn.fst.spring.projet_spring.entities.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.entities.payment.Invoice;
import tn.fst.spring.projet_spring.entities.payment.Payment;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItem> items = new HashSet<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Invoice invoice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private DeliveryRequest deliveryRequest;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<Complaint> complaints = new HashSet<>();

    public void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    public void confirmOrder() {
        this.status = OrderStatus.CONFIRMED;
    }
}


