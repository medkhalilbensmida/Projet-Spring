package tn.fst.spring.projet_spring.controllers.statUserProduct;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.fst.spring.projet_spring.dto.statUserProduct.*;
import tn.fst.spring.projet_spring.services.interfaces.IUserProductStatsService;


@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class UserProductStatsController {
    private final IUserProductStatsService statsService;

    @GetMapping("/users")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        return ResponseEntity.ok(statsService.getUserStats());
    }

    @GetMapping("/products")
    public ResponseEntity<ProductStatsDTO> getProductStats() {
        return ResponseEntity.ok(statsService.getProductStats());
    }

    @GetMapping("/inventory")
    public ResponseEntity<InventoryStatsDTO> getInventoryStats() {
        return ResponseEntity.ok(statsService.getInventoryStats());
    }

    @GetMapping("/barcode")
    public ResponseEntity<BarcodeStatsDTO> getBarcodeStats() {
        return ResponseEntity.ok(statsService.getBarcodeStats());
    }
}