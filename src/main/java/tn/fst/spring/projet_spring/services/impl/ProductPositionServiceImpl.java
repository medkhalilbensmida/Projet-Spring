package tn.fst.spring.projet_spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.model.catalog.ProductPosition;
import tn.fst.spring.projet_spring.repositories.products.ProductPositionRepository;
import tn.fst.spring.projet_spring.services.interfaces.IProductPositionService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductPositionServiceImpl implements IProductPositionService {

    @Autowired
    private ProductPositionRepository productPositionRepository;

    @Override
    public ProductPosition createProductPosition(ProductPosition productPosition) {
        return productPositionRepository.save(productPosition);
    }

    @Override
    public Optional<ProductPosition> getProductPosition(Long id) {
        return productPositionRepository.findById(id);
    }

    @Override
    public List<ProductPosition> getAllProductPositions() {
        return productPositionRepository.findAll();
    }

    @Override
    public ProductPosition updateProductPosition(Long id, ProductPosition updatedPosition) {
        Optional<ProductPosition> existingPosition = productPositionRepository.findById(id);

        if (existingPosition.isPresent()) {
            ProductPosition position = existingPosition.get();
            position.setX(updatedPosition.getX());
            position.setY(updatedPosition.getY());
            position.setWidth(updatedPosition.getWidth());
            position.setHeight(updatedPosition.getHeight());
            position.setZIndex(updatedPosition.getZIndex());
            return productPositionRepository.save(position);
        }
        return null;  // ou une exception personnalisée
    }

    @Override
    public void deleteProductPosition(Long id) {
        productPositionRepository.deleteById(id);
    }
}
