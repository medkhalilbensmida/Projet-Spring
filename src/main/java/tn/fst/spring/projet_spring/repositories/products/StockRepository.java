package tn.fst.spring.projet_spring.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.fst.spring.projet_spring.model.catalog.Stock;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    long countByQuantity(int quantity);

    @Query("SELECT COUNT(s) FROM Stock s WHERE s.quantity < ?1")
    long countByQuantityLessThan(int threshold);

    @Query("SELECT SUM(s.quantity) FROM Stock s")
    Long sumQuantity();

    @Query("SELECT s FROM Stock s ORDER BY s.quantity DESC LIMIT 1")
    Optional<Stock> findTopByOrderByQuantityDesc();

    @Query("SELECT s FROM Stock s ORDER BY s.quantity ASC LIMIT 1")
    Optional<Stock> findTopByOrderByQuantityAsc();
}