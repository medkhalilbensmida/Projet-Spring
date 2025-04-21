package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.fst.spring.projet_spring.dto.products.*;
import tn.fst.spring.projet_spring.model.catalog.Category;
import tn.fst.spring.projet_spring.model.catalog.Product;
import tn.fst.spring.projet_spring.model.catalog.Stock;
import tn.fst.spring.projet_spring.repositories.products.CategoryRepository;
import tn.fst.spring.projet_spring.repositories.products.ProductRepository;
import tn.fst.spring.projet_spring.services.interfaces.IProductService;

import jakarta.persistence.criteria.Predicate;
import tn.fst.spring.projet_spring.services.utils.BarcodeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    private BarcodeService barcodeService;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable  !"));
        return convertToResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        if (!verifyTunisianBarcode(productRequest.getBarcode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code-barres invalide : doit commencer par 619 et contenir 13 chiffres.");
        }

        if (productRepository.findByBarcode(productRequest.getBarcode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    " Le code-barres ' " + productRequest.getBarcode() + " ' est déjà utilisé par un autre produit.");
        }

        Category category = categoryRepository.findByName(productRequest.getCategoryName())
                .orElseThrow(() -> {
                    List<String> categories = categoryRepository.findAll().stream()
                            .map(Category::getName)
                            .toList();
                    String message = String.format("Catégorie << %s >> introuvable ! Voici la liste des catégories valides : %s",
                            productRequest.getCategoryName(), categories);
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
                });

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setBarcode(productRequest.getBarcode());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(category);
        product.setWeight(productRequest.getWeight());

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(productRequest.getInitialQuantity());
        stock.setMinThreshold(productRequest.getMinThreshold());
        product.setStock(stock);

        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable  !"));

        Category category = categoryRepository.findByName(productRequest.getCategoryName())
                .orElseThrow(() -> {
                    List<String> categories = categoryRepository.findAll().stream()
                            .map(Category::getName)
                            .toList();
                    String message = String.format("Catégorie << %s >> introuvable ! Voici les catégories valides : %s",
                            productRequest.getCategoryName(), categories);
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
                });

        product.setName(productRequest.getName());
        product.setBarcode(productRequest.getBarcode());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(category);

        if (productRequest.getWeight() != null) {
            product.setWeight(productRequest.getWeight());
        }

        if (product.getStock() != null) {
            product.getStock().setQuantity(productRequest.getStockQuantity());
            product.getStock().setMinThreshold(productRequest.getMinThreshold());
        }

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable  !");
        }
        productRepository.deleteById(id);
    }

    @Override
    public boolean verifyTunisianBarcode(String barcode) {
        return barcode != null && barcode.startsWith("619") && barcode.length() == 13;
    }

    @Override
    public Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchRequest.getName() != null && !searchRequest.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + searchRequest.getName().toLowerCase() + "%"));
            }

            if (searchRequest.getBarcode() != null && !searchRequest.getBarcode().isEmpty()) {
                predicates.add(cb.equal(root.get("barcode"), searchRequest.getBarcode()));
            }

            if (searchRequest.getCategoryName() != null && !searchRequest.getCategoryName().isEmpty()) {
                predicates.add(cb.equal(root.get("category").get("name"), searchRequest.getCategoryName()));
            }

            if (searchRequest.getMinWeight() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("weight"), searchRequest.getMinWeight()));
            }

            if (searchRequest.getMaxWeight() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("weight"), searchRequest.getMaxWeight()));
            }

            if (searchRequest.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), searchRequest.getMinPrice()));
            }

            if (searchRequest.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), searchRequest.getMaxPrice()));
            }

            if (searchRequest.getMinStock() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("stock").get("quantity"), searchRequest.getMinStock()));
            }

            if (searchRequest.getMaxStock() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("stock").get("quantity"), searchRequest.getMaxStock()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                Sort.by(searchRequest.getSortDirection(), searchRequest.getSortBy())
        );

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::convertToResponse);
    }

    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .barcode(product.getBarcode())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory().getName())
                .stockQuantity(product.getStock().getQuantity())
                .weight(product.getWeight())
                .build();
    }

    @Override
    public BarcodeExtractionResponse extractBarcode(MultipartFile file) {
        return barcodeService.extractBarcodeFromImage(file);
    }

    @Override
    public ProductResponse extractProductDetailsFromBarcodeImage(MultipartFile file) {
        BarcodeExtractionResponse extraction = barcodeService.extractBarcodeFromImage(file);

        if (!extraction.isTunisian()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Code-barres détecté : " + extraction.getBarcode() + ". Ce n’est pas un produit tunisien (doit commencer par 619).");
        }

        Product product = productRepository.findByBarcode(extraction.getBarcode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aucun produit trouvé pour le code-barres : " + extraction.getBarcode()));

        return convertToResponse(product);
    }


}
