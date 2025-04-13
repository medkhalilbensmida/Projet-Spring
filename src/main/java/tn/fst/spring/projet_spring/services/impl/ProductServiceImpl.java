package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.fst.spring.projet_spring.dto.products.ProductRequest;
import tn.fst.spring.projet_spring.dto.products.ProductResponse;
import tn.fst.spring.projet_spring.dto.products.ProductSearchRequest;
import tn.fst.spring.projet_spring.model.catalog.Category;
import tn.fst.spring.projet_spring.model.catalog.Product;
import tn.fst.spring.projet_spring.model.catalog.Stock;
import tn.fst.spring.projet_spring.repositories.products.CategoryRepository;
import tn.fst.spring.projet_spring.repositories.products.ProductRepository;
import tn.fst.spring.projet_spring.services.interfaces.IProductService;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return convertToResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        if (!verifyTunisianBarcode(productRequest.getBarcode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Tunisian barcode");
        }

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setBarcode(productRequest.getBarcode());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(category);

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(productRequest.getInitialQuantity());
        stock.setMinThreshold(productRequest.getMinThreshold());
        product.setStock(stock);

        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
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
                .build();
    }

}