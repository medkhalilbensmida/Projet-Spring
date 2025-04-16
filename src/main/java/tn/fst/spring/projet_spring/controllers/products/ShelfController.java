package tn.fst.spring.projet_spring.controllers.products;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.products.ShelfRequest;
import tn.fst.spring.projet_spring.dto.products.ShelfResponse;
import tn.fst.spring.projet_spring.services.interfaces.IShelfService;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
public class ShelfController {

    private final IShelfService shelfService;

    @PostMapping
    public ResponseEntity<ShelfResponse> create(@RequestBody ShelfRequest request) {
        return ResponseEntity.ok(shelfService.createShelf(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShelfResponse> getById(@PathVariable Long id) {
        return shelfService.getShelf(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ShelfResponse> getAll() {
        return shelfService.getAllShelves();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelfResponse> update(@PathVariable Long id, @RequestBody ShelfRequest request) {
        return ResponseEntity.ok(shelfService.updateShelf(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shelfService.deleteShelf(id);
        return ResponseEntity.noContent().build();
    }

    // 🔎 Custom Queries

    @GetMapping("/by-product/{productId}")
    public List<ShelfResponse> findByProduct(@PathVariable Long productId) {
        return shelfService.findShelvesByProduct(productId);
    }

    @GetMapping("/by-type")
    public List<ShelfResponse> findByType(@RequestParam String type) {
        return shelfService.searchShelvesByType(type);
    }

    @GetMapping("/by-coordinates")
    public List<ShelfResponse> findByCoordinates(@RequestParam int x, @RequestParam int y) {
        return shelfService.searchShelvesByCoordinates(x, y);
    }

    @GetMapping("/between-coordinates")
    public List<ShelfResponse> findBetweenCoordinates(
            @RequestParam int xmin,
            @RequestParam int xmax,
            @RequestParam int ymin,
            @RequestParam int ymax) {
        return shelfService.searchShelvesBetweenCoordinates(xmin, xmax, ymin, ymax);
    }

    @GetMapping("/by-name")
    public List<ShelfResponse> findByName(@RequestParam String name) {
        return shelfService.findShelvesByName(name);
    }
}
