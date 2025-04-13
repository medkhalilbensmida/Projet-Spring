package tn.fst.spring.projet_spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.fst.spring.projet_spring.dto.products.ProductPositionRequest;
import tn.fst.spring.projet_spring.dto.products.ProductPositionResponse;
import tn.fst.spring.projet_spring.dto.products.ProductPositionSearchRequest;
import tn.fst.spring.projet_spring.model.catalog.ProductPosition;
import tn.fst.spring.projet_spring.repositories.products.ProductPositionRepository;
import tn.fst.spring.projet_spring.services.interfaces.IProductPositionService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductPositionServiceImpl implements IProductPositionService {

    @Autowired
    private ProductPositionRepository productPositionRepository;

    private ProductPosition toEntity(ProductPositionRequest request) {
        ProductPosition position = new ProductPosition();
        position.setX(request.getX());
        position.setY(request.getY());
        position.setWidth(request.getWidth());
        position.setHeight(request.getHeight());
        position.setZIndex(request.getZIndex());
        return position;
    }

    private ProductPositionResponse toResponse(ProductPosition productPosition){
        ProductPositionResponse response = new ProductPositionResponse();
        response.setX(productPosition.getX());
        response.setY(productPosition.getY());
        response.setWidth(productPosition.getWidth());
        response.setHeight(productPosition.getHeight());
        response.setZIndex(productPosition.getZIndex());
        return response;
    }

    @Override
    public ProductPositionResponse createProductPosition(ProductPositionRequest productPosition) {
        ProductPosition positionEntity = toEntity(productPosition);
        ProductPosition position = productPositionRepository.save(positionEntity);
        return toResponse(position);
    }

    @Override
    public Optional<ProductPositionResponse> getProductPosition(Long id) {
        Optional<ProductPosition> position = productPositionRepository.findById(id);
        if (position.isPresent()) {
            return Optional.of(toResponse(position.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<ProductPositionResponse> getAllProductPositions() {
        return productPositionRepository.findAll().
                stream()
                .map(this::toResponse)
                .toList(); 
    }

    @Override
    public ProductPositionResponse updateProductPosition(Long id, ProductPositionRequest updatedPosition) {
        Optional<ProductPosition> existingPosition = productPositionRepository.findById(id);

        if (existingPosition.isPresent()) {
            ProductPosition position = existingPosition.get();
            position.setX(updatedPosition.getX());
            position.setY(updatedPosition.getY());
            position.setWidth(updatedPosition.getWidth());
            position.setHeight(updatedPosition.getHeight());
            position.setZIndex(updatedPosition.getZIndex());
            ProductPosition newposition = productPositionRepository.save(position);
            return toResponse(newposition);
        }
        return null;  // ou une exception personnalisée
    }

    @Override
    public void deleteProductPosition(Long id) {
        productPositionRepository.deleteById(id);
    }

    @Override
    public List<ProductPositionResponse> findPositionsByShelf(Long shelfId) {
        return productPositionRepository.findByShelfId(shelfId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductPositionResponse> findPositionsByProduct(Long productId) {
        return productPositionRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductPositionResponse> searchProductPositions(ProductPositionSearchRequest request) {
        return productPositionRepository.searchByFilters(
                request.getProductId(),
                request.getShelfId(),
                request.getXmin(),
                request.getXmax(),
                request.getYmin(),
                request.getYmax()
        ).stream()
        .map(this::toResponse)
        .toList();
    }

}
