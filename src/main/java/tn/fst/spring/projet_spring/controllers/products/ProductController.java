package tn.fst.spring.projet_spring.controllers.products;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.products.ProductRequest;
import tn.fst.spring.projet_spring.dto.products.ProductResponse;
import tn.fst.spring.projet_spring.dto.products.ProductSearchRequest;
import tn.fst.spring.projet_spring.services.interfaces.IProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.createProduct(productRequest));
    }

    @Operation(summary = "Update a product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verify Tunisian barcode")
    @GetMapping("/verify-barcode/{barcode}")
    public ResponseEntity<Boolean> verifyBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(productService.verifyTunisianBarcode(barcode));
    }

    @Operation(summary = "Search products with advanced filters",
            parameters = {
                    @Parameter(name = "name", in = ParameterIn.QUERY, description = "Product name filter"),
                    @Parameter(name = "barcode", in = ParameterIn.QUERY, description = "Barcode filter"),
                    @Parameter(name = "categoryName", in = ParameterIn.QUERY, description = "Category name filter",
                            schema = @Schema(type = "string", allowableValues = {"Alimentation", "Artisanat", "Cosmétique"})),
                    @Parameter(name = "minPrice", in = ParameterIn.QUERY, description = "Minimum price filter",
                            schema = @Schema(type = "number", format = "double")),
                    @Parameter(name = "maxPrice", in = ParameterIn.QUERY, description = "Maximum price filter",
                            schema = @Schema(type = "number", format = "double")),
                    @Parameter(name = "minStock", in = ParameterIn.QUERY, description = "Minimum stock quantity filter",
                            schema = @Schema(type = "integer")),
                    @Parameter(name = "maxStock", in = ParameterIn.QUERY, description = "Maximum stock quantity filter",
                            schema = @Schema(type = "integer")),
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number (0-based)",
                            schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size",
                            schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sortBy", in = ParameterIn.QUERY, description = "Field to sort by",
                            schema = @Schema(type = "string", allowableValues = {
                                    "name", "price", "stockQuantity", "category.name"
                            }, defaultValue = "name")),
                    @Parameter(name = "sortDirection", in = ParameterIn.QUERY, description = "Sort direction",
                            schema = @Schema(type = "string", allowableValues = {
                                    "ASC", "DESC"
                            }, defaultValue = "ASC"))
            })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        ProductSearchRequest searchRequest = new ProductSearchRequest();
        searchRequest.setName(name);
        searchRequest.setBarcode(barcode);
        searchRequest.setCategoryName(categoryName);
        searchRequest.setMinPrice(minPrice);
        searchRequest.setMaxPrice(maxPrice);
        searchRequest.setMinStock(minStock);
        searchRequest.setMaxStock(maxStock);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(org.springframework.data.domain.Sort.Direction.fromString(sortDirection));

        return ResponseEntity.ok(productService.searchProducts(searchRequest));
    }
}