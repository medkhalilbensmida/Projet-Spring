package tn.fst.spring.projet_spring.controllers.logistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tn.fst.spring.projet_spring.dto.logistics.CreateDeliveryRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.DeliveryRequestDTO;
import tn.fst.spring.projet_spring.exception.ResourceNotFoundException;
import tn.fst.spring.projet_spring.services.logistics.IDeliveryRequestService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveryRequests")
@RequiredArgsConstructor
public class DeliveryRequestController {

    private final IDeliveryRequestService deliveryRequestService;

    @PostMapping
    public ResponseEntity<DeliveryRequestDTO> create(@Valid @RequestBody CreateDeliveryRequestDTO dto) {
        DeliveryRequestDTO created = deliveryRequestService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

   
    @GetMapping("/estimate-fee")
    public ResponseEntity<Map<String, Object>> estimateFee(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double weightKg) {
        try {
            double fee = deliveryRequestService.calculateDeliveryFee(latitude, longitude, weightKg);
            return ResponseEntity.ok(Map.of("deliveryFee", (Object)fee));
        } catch (IllegalArgumentException e) {
            // Return a 400 Bad Request if input validation fails in the service
            return ResponseEntity.badRequest().body(Map.of("error", (Object)e.getMessage()));
        }
    }

    // New endpoint to calculate fee for a specific DeliveryRequest
    @GetMapping("/{id}/fee")
    public ResponseEntity<Map<String, Object>> getFeeForRequest(@PathVariable Long id) {
        try {
            double fee = deliveryRequestService.calculateFeeForRequest(id);
            return ResponseEntity.ok(Map.of("deliveryFee", (Object)fee));
        } catch (ResourceNotFoundException e) {
            // Handles cases where DeliveryRequest, Order, or coordinates are missing
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", (Object)e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Should ideally not happen if validation is in service, but good practice
            return ResponseEntity.badRequest().body(Map.of("error", (Object)e.getMessage()));
        } catch (Exception e) {
            // Catch unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", (Object)"An unexpected error occurred calculating the fee."));
        }
    }
}
