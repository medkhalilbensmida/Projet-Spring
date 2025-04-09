package tn.fst.spring.projet_spring.services.catalog;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import tn.fst.spring.projet_spring.entities.catalog.Category;
import tn.fst.spring.projet_spring.entities.catalog.Product;
import tn.fst.spring.projet_spring.repositories.catalog.CategoryRepository;
import tn.fst.spring.projet_spring.repositories.catalog.ProductRepository;
import tn.fst.spring.projet_spring.repositories.catalog.ProductSpecification;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final BarCodeExtractor barCodeExtractor;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    // Recherche avec filtres pour nom, prix min/max et catégorie
    public List<Product> searchByFilters(String name, Double minPrice, Double maxPrice, Long category) {
        // Récupérer la catégorie par son ID
        Category categoryEntity = null;
        if (category != null) {
            categoryEntity = categoryRepository.findById(category).orElse(null);
        }

        Specification<Product> spec = ProductSpecification.withFilters(name, minPrice, maxPrice, categoryEntity);
        return productRepository.findAll(spec);
    }

    public Boolean VerifyProductbarCode(MultipartFile file) {
        try {
            String barcode = barCodeExtractor.extraireCodeBarre(file);

            if (barcode != null && barcode.length() == 13) {
                // Vérifie si les 3 premiers chiffres sont "619"
                return barcode.startsWith("619");
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    

}
