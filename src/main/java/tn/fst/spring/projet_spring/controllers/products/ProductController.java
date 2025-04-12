package tn.fst.spring.projet_spring.controllers.products;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.fst.spring.projet_spring.dto.products.*;
import tn.fst.spring.projet_spring.services.interfaces.IProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @Operation(summary = "Lister tous les produits")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Récupérer un produit par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Créer un nouveau produit")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.createProduct(productRequest));
    }

    @Operation(summary = "Mettre à jour un produit existant")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest updateRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, updateRequest));
    }


    @Operation(summary = "Supprimer un produit par ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vérifier si le code-barres est tunisien (commence par 619)")
    @GetMapping("/verify-barcode/{barcode}")
    public ResponseEntity<Boolean> verifyBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(productService.verifyTunisianBarcode(barcode));
    }

    @Operation(summary = "Rechercher les produits avec des filtres avancés",
            parameters = {
                    @Parameter(name = "name", in = ParameterIn.QUERY, description = "Filtrer par nom de produit"),
                    @Parameter(name = "barcode", in = ParameterIn.QUERY, description = "Filtrer par code-barres"),
                    @Parameter(name = "categoryName", in = ParameterIn.QUERY, description = "Filtrer par nom de catégorie",
                            schema = @Schema(type = "string", allowableValues = {"Alimentation", "Artisanat", "Cosmétique"})),
                    @Parameter(name = "minPrice", in = ParameterIn.QUERY, description = "Prix minimum",
                            schema = @Schema(type = "number", format = "double")),
                    @Parameter(name = "maxPrice", in = ParameterIn.QUERY, description = "Prix maximum",
                            schema = @Schema(type = "number", format = "double")),
                    @Parameter(name = "minStock", in = ParameterIn.QUERY, description = "Quantité de stock minimum",
                            schema = @Schema(type = "integer")),
                    @Parameter(name = "maxStock", in = ParameterIn.QUERY, description = "Quantité de stock maximum",
                            schema = @Schema(type = "integer")),
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Numéro de page (0-based)",
                            schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Taille de la page",
                            schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sortBy", in = ParameterIn.QUERY, description = "Champ de tri",
                            schema = @Schema(type = "string", allowableValues = {
                                    "name", "price", "stockQuantity", "category.name"
                            }, defaultValue = "name")),
                    @Parameter(name = "sortDirection", in = ParameterIn.QUERY, description = "Ordre de tri",
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

    @Operation(summary = "Extraire le code-barres depuis une image uploadée")
    @PostMapping(value = "/extract-barcode", consumes = "multipart/form-data")
    public ResponseEntity<BarcodeExtractionResponse> extractBarcodeFromImage(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.extractBarcode(file));
    }





}
