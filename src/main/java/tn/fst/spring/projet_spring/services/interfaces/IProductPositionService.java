package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.model.catalog.ProductPosition;

import java.util.List;
import java.util.Optional;

public interface IProductPositionService {

    ProductPosition createProductPosition(ProductPosition productPosition);

    Optional<ProductPosition> getProductPosition(Long id);

    List<ProductPosition> getAllProductPositions();

    ProductPosition updateProductPosition(Long id, ProductPosition updatedPosition);

    void deleteProductPosition(Long id);
}
