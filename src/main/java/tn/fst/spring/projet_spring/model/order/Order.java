package tn.fst.spring.projet_spring.model.order;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.logistics.Complaint;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.payment.Invoice;
import tn.fst.spring.projet_spring.model.payment.Payment;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"items", "user", "payment", "invoice", "deliveryRequest", "complaints"})
@EqualsAndHashCode(exclude = {"items", "user", "payment", "invoice", "deliveryRequest", "complaints"})
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

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
    @Column(nullable = false)
    private SaleType saleType;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();

    // Modified cascade type to prevent accidental deletion
    @OneToOne(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Invoice invoice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private DeliveryRequest deliveryRequest;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<Complaint> complaints = new HashSet<>();
    
    // For door-to-door sales
    private String customerAddress;
    private String customerPhone;
    private String salespersonNote;

    public void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    public void confirmOrder() {
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        calculateTotal();
    }

    /**
     * Links this order with a payment and updates order status
     * @param payment The payment to associate with this order
     */
    public void attachPayment(Payment payment) {
        this.payment = payment;
        if (payment != null && payment.isSuccessful()) {
            this.status = OrderStatus.CONFIRMED;
        }
    }
}