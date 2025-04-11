package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.Data;
import tn.fst.spring.projet_spring.model.donation.Donation;
import tn.fst.spring.projet_spring.model.order.OrderItem;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String barcode;

    private String description;

    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Stock stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductPosition> positions = new HashSet<>();
    

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<Donation> donations = new HashSet<>();

    public boolean validateBarcode() {
        // Implementation of barcode validation logic
        return barcode != null && barcode.matches("^\\d{12,13}$");
    }
}