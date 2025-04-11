package tn.fst.spring.projet_spring.repositories.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.fst.spring.projet_spring.model.catalog.ProductPosition;

public interface ProductPositionRepository extends JpaRepository<ProductPosition, Long> {

    List<ProductPosition> findByProductId(Long productId);
    List<ProductPosition> findByLocation(String location);
    
}
