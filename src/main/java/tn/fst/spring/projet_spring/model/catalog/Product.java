package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tn.fst.spring.projet_spring.model.donation.Donation;
import tn.fst.spring.projet_spring.model.order.OrderItem;

import java.util.HashSet;
import java.util.Set;

/**
 * @author claudia
 */

@Getter
@Setter
//to avoid stack overflow error when using toString or equalsAndHashCode
@ToString(exclude = {"stock", "orderItems", "donations", "shelves"})
@EqualsAndHashCode(exclude = {"stock", "orderItems", "donations", "shelves"})

@Entity

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String barcode;

    private double weight; // Weight in kilograms

    private String description;

    @Column(nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
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
        return barcode != null && barcode.matches("^\\d{12,13}$");
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
