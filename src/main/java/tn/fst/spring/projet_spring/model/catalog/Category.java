package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
//to avoid stack overflow error when using toString or equalsAndHashCode
@ToString(exclude = {"products"})
@EqualsAndHashCode(exclude = {"products"})
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    // Constructeur pratique
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category() {}
}