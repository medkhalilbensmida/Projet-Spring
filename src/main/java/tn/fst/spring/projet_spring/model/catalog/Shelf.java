package tn.fst.spring.projet_spring.model.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString(exclude = "products") // exclure produits pour éviter lazy init
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type;

    @ManyToMany(mappedBy = "shelves", fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    public Shelf(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Shelf() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shelf shelf)) return false;
        return Objects.equals(id, shelf.id) &&
                Objects.equals(name, shelf.name) &&
                Objects.equals(type, shelf.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}
