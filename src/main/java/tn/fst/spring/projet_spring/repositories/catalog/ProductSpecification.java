package tn.fst.spring.projet_spring.repositories.catalog;

import org.springframework.data.jpa.domain.Specification;
import tn.fst.spring.projet_spring.entities.catalog.Product;
import tn.fst.spring.projet_spring.entities.catalog.Category;

public class ProductSpecification {

    // Spécification pour rechercher par nom
    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    // Spécification pour rechercher par prix min
    public static Specification<Product> hasMinPrice(Double minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    // Spécification pour rechercher par prix max
    public static Specification<Product> hasMaxPrice(Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    // Spécification pour rechercher par catégorie
    public static Specification<Product> hasCategory(Category category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    // Spécification pour combiner nom, prix min/max et catégorie
    public static Specification<Product> withFilters(String name, Double minPrice, Double maxPrice, Category category) {
        return Specification
            .where(hasName(name))
            .and(hasMinPrice(minPrice))
            .and(hasMaxPrice(maxPrice))
            .and(hasCategory(category));
    }
}
