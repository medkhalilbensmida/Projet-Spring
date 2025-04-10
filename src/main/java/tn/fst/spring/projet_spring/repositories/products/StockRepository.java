package tn.fst.spring.projet_spring.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.catalog.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}