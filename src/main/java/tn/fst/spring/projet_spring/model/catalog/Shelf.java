    package tn.fst.spring.projet_spring.model.catalog;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
    import java.util.Set;

    @Data
    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    public class Shelf {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        private String type;
        @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL)
        private Set<ProductPosition> positions = new HashSet<>();
        
        @Column
        private int x;

        @Column
        private int y;

        @Column
        private int width; // largeur du rayon
        @Column
        private int height; // hauteur du rayon

        public Shelf(String name, String type, int x, int y, int width, int height) {
            this.name = name;
            this.type = type;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

}