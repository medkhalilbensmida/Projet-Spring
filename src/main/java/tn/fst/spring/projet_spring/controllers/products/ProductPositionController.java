package tn.fst.spring.projet_spring.controllers.products;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.products.ProductPositionRequest;
import tn.fst.spring.projet_spring.dto.products.ProductPositionResponse;
import tn.fst.spring.projet_spring.dto.products.ProductPositionSearchRequest;
import tn.fst.spring.projet_spring.services.interfaces.IProductPositionService;

import java.util.List;

@RestController
@RequestMapping("/api/product-positions")
@RequiredArgsConstructor
public class ProductPositionController {

    private final IProductPositionService positionService;

    @PostMapping
    public ResponseEntity<ProductPositionResponse> create(@RequestBody ProductPositionRequest request) {
        return ResponseEntity.ok(positionService.createProductPosition(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductPositionResponse> getById(@PathVariable Long id) {
        return positionService.getProductPosition(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProductPositionResponse> getAll() {
        return positionService.getAllProductPositions();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductPositionResponse> update(
            @PathVariable Long id,
            @RequestBody ProductPositionRequest request) {
        return ResponseEntity.ok(positionService.updateProductPosition(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        positionService.deleteProductPosition(id);
        return ResponseEntity.noContent().build();
    }

    // 🔎 Custom Queries

    @GetMapping("/by-shelf/{shelfId}")
    public List<ProductPositionResponse> findByShelf(@PathVariable Long shelfId) {
        return positionService.findPositionsByShelf(shelfId);
    }

    @GetMapping("/by-product/{productId}")
    public List<ProductPositionResponse> findByProduct(@PathVariable Long productId) {
        return positionService.findPositionsByProduct(productId);
    }

    @PostMapping("/search")
    public List<ProductPositionResponse> search(@RequestBody ProductPositionSearchRequest searchRequest) {
        return positionService.searchProductPositions(searchRequest);
    }
}
