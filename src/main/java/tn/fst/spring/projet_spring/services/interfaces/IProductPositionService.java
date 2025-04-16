package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.products.ProductPositionRequest;
import tn.fst.spring.projet_spring.dto.products.ProductPositionResponse;
import tn.fst.spring.projet_spring.dto.products.ProductPositionSearchRequest;
import tn.fst.spring.projet_spring.model.catalog.ProductPosition;

import java.util.List;
import java.util.Optional;



public interface IProductPositionService {

    ProductPositionResponse createProductPosition(ProductPositionRequest productPosition);
    Optional<ProductPositionResponse> getProductPosition(Long id);
    List<ProductPositionResponse> getAllProductPositions();
    ProductPositionResponse updateProductPosition(Long id, ProductPositionRequest updatedPosition);
    void deleteProductPosition(Long id);

    List<ProductPositionResponse> findPositionsByShelf(Long shelfId);
    List<ProductPositionResponse> findPositionsByProduct(Long productId);
    List<ProductPositionResponse> searchProductPositions(ProductPositionSearchRequest request);
    


}
