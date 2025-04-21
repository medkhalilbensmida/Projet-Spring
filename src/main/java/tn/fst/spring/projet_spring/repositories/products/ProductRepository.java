package tn.fst.spring.projet_spring.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tn.fst.spring.projet_spring.model.catalog.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByCategoryName(String categoryName);
    List<Product> findByNameContainingIgnoreCase(String name);
    Optional<Product> findByBarcode(String barcode);

    @Query("SELECT p FROM Product p ORDER BY p.price DESC LIMIT 1")
    Optional<Product> findTopByOrderByPriceDesc();

    @Query("SELECT p FROM Product p ORDER BY p.price ASC LIMIT 1")
    Optional<Product> findTopByOrderByPriceAsc();

    @Query("SELECT AVG(p.price) FROM Product p")
    Double getAveragePrice();

    @Query("SELECT SUM(p.price * s.quantity) FROM Product p JOIN p.stock s")
    Double calculateTotalInventoryValue();

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.category.name = ?1")
    Double getAveragePriceByCategory(String categoryName);
}