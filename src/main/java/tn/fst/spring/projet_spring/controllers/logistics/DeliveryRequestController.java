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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tn.fst.spring.projet_spring.exception.DeliveryAlreadyAssignedException;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveryRequests")
@RequiredArgsConstructor
@Tag(name = "Delivery Request Management", description = "APIs for managing delivery requests")
public class DeliveryRequestController {

    private final IDeliveryRequestService deliveryRequestService;

    @Operation(summary = "Create a new delivery request", description = "Creates a new delivery request with the specified information.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Delivery request successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Order or livreur not found")
    })
    @PostMapping
    public ResponseEntity<DeliveryRequestDTO> create(@Valid @RequestBody CreateDeliveryRequestDTO dto) {
        DeliveryRequestDTO created = deliveryRequestService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Estimate delivery fee", description = "Estimates the delivery fee based on destination and package weight.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fee successfully calculated"),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
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

    @Operation(summary = "Calculate fee for a delivery request", description = "Calculates the delivery fee for an existing delivery request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fee successfully calculated"),
        @ApiResponse(responseCode = "404", description = "Delivery request not found")
    })
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

    @Operation(summary = "Auto-assign livreur to delivery request", 
              description = "Automatically assigns the closest available livreur to a delivery request based on their location.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livreur successfully assigned"),
        @ApiResponse(responseCode = "404", description = "Delivery request not found or no available livreurs"),
        @ApiResponse(responseCode = "409", description = "Delivery request already assigned")
    })
    @PostMapping("/{id}/assign-livreur")
    public ResponseEntity<Map<String, Object>> autoAssignLivreur(@PathVariable Long id) {
        try {
            DeliveryRequestDTO updated = deliveryRequestService.autoAssignLivreur(id);
            return ResponseEntity.ok(Map.of(
                "message", "Livreur assigned successfully",
                "assignedLivreurId", updated.getLivreurId() != null ? updated.getLivreurId() : "N/A",
                "deliveryRequest", updated
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", (Object)e.getMessage()));
        } catch (DeliveryAlreadyAssignedException e) {
            // Return 409 Conflict when already assigned
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", (Object)e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", (Object)"An error occurred during auto-assignment: " + e.getMessage()));
        }
    }
}
