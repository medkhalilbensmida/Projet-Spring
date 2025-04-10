package tn.fst.spring.projet_spring.controller.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.entities.catalog.Product;
import tn.fst.spring.projet_spring.services.catalog.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Recherche avec des filtres dynamiques : nom, prix min, prix max, catégorie
    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "minPrice", required = false) Double minPrice,
                                                 @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                                 @RequestParam(value = "categoryId", required = false) Long categoryId) {

        List<Product> products = productService.searchByFilters(name, minPrice, maxPrice, categoryId);
        return ResponseEntity.ok(products);
    }
}
