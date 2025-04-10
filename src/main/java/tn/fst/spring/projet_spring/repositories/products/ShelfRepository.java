package tn.fst.spring.projet_spring.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.entities.catalog.Shelf;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
}