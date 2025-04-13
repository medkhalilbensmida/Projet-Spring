package tn.fst.spring.projet_spring.repositories.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.catalog.Shelf;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    List<Shelf> findByPositionsProductId(Long productId);
    List<Shelf> findByType(String type);
    List<Shelf> findByXAndY(int x, int y);
    List<Shelf> findByXBetweenAndYBetween(int x1, int x2, int y1, int y2);
    List<Shelf> findByName(String name);
}