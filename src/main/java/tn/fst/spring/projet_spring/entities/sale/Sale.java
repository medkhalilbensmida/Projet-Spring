package tn.fst.spring.projet_spring.entities.sale;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.entities.auth.User;
import tn.fst.spring.projet_spring.entities.order.Order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime saleDate;

    @Column(nullable = false)
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleType saleType;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private Set<SaleItem> saleItems = new HashSet<>();
}