package tn.fst.spring.projet_spring.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.catalog.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}