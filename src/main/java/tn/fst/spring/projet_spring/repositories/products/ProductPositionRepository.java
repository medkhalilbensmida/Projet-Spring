package tn.fst.spring.projet_spring.repositories.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tn.fst.spring.projet_spring.model.catalog.ProductPosition;

public interface ProductPositionRepository extends JpaRepository<ProductPosition, Long> {

    List<ProductPosition> findByProductId(Long productId);
    List<ProductPosition> findByShelfId(Long shelfId);

    @Query(
        "SELECT p FROM ProductPosition p " +
        "WHERE (:productId IS NULL OR p.product.id = :productId) " +
        "AND (:shelfId IS NULL OR p.shelf.id = :shelfId) " +
        "AND (:xmin IS NULL OR p.x >= :xmin) " +
        "AND (:xmax IS NULL OR p.x <= :xmax) " +
        "AND (:ymin IS NULL OR p.y >= :ymin) " +
        "AND (:ymax IS NULL OR p.y <= :ymax)")
    List<ProductPosition> searchByFilters(
        @Param("productId") Long productId,
        @Param("shelfId") Long shelfId,
        @Param("xmin") Integer xmin,
        @Param("xmax") Integer xmax,
        @Param("ymin") Integer ymin,
        @Param("ymax") Integer ymax
    );

    
}
