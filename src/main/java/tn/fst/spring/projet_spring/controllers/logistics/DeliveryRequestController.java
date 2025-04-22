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
import tn.fst.spring.projet_spring.dto.logistics.UpdateDeliveryDestinationDTO;
import tn.fst.spring.projet_spring.dto.logistics.UpdateDeliveryStatusDTO;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import lombok.extern.slf4j.Slf4j;
import tn.fst.spring.projet_spring.exception.GeocodingException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveryRequests")
@RequiredArgsConstructor
@Tag(name = "Delivery Request Management", description = "APIs for managing delivery requests")
@Slf4j
public class DeliveryRequestController {

    private final IDeliveryRequestService deliveryRequestService;

    @Operation(summary = "Retrieve all delivery requests", 
               description = "Gets a list of all delivery requests, optionally filtered by status, livreur ID, or order ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameter value"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<DeliveryRequestDTO>> getAllDeliveryRequests(
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Long livreurId,
            @RequestParam(required = false) Long orderId
    ) {
        log.info("Received request to get all delivery requests with filters - Status: {}, LivreurID: {}, OrderID: {}", 
                 status, livreurId, orderId);
        try {
            List<DeliveryRequestDTO> requests = deliveryRequestService.getAllDeliveryRequests(status, livreurId, orderId);
            if (requests.isEmpty()) {
                return ResponseEntity.noContent().build(); // Return 204 if no requests match
            } else {
                return ResponseEntity.ok(requests);
            }
        } catch (Exception e) {
            log.error("Error retrieving delivery requests with filters - Status: {}, LivreurID: {}, OrderID: {}", 
                      status, livreurId, orderId, e);
            // Return 500 for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null); // Avoid sending error details in production for generic errors
        }
    }

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

    @Operation(summary = "Update delivery request destination",
               description = "Updates the destination coordinates of a specific delivery request and recalculates the delivery fee.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Destination updated and fee recalculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., invalid coordinates)"),
        @ApiResponse(responseCode = "404", description = "Delivery request not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error during update or calculation")
    })
    @PatchMapping("/{id}/destination") // Using PATCH as we are partially updating the resource
    public ResponseEntity<?> updateDestination(@PathVariable Long id, @Valid @RequestBody UpdateDeliveryDestinationDTO updateDto) {
        try {
            DeliveryRequestDTO updatedDto = deliveryRequestService.updateDestinationAndRecalculateFee(id, updateDto);
            return ResponseEntity.ok(updatedDto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", (Object)e.getMessage()));
        } catch (IllegalArgumentException e) { // Catch validation errors from DTO or service
            return ResponseEntity.badRequest().body(Map.of("error", (Object)e.getMessage()));
        } catch (IllegalStateException e) { // Catch potential state issues (e.g., order missing, cannot update status)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", (Object)e.getMessage()));
        } catch (Exception e) {
            // Generic catch for other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", (Object)("An unexpected error occurred: " + e.getMessage())));
        }
    }

    @Operation(summary = "Update delivery request status",
               description = "Updates the status of a specific delivery request. Handles livreur availability based on status transitions.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., invalid status)"),
        @ApiResponse(responseCode = "404", description = "Delivery request not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - e.g., trying to change status from SUCCESSFUL, or invalid transition."),
        @ApiResponse(responseCode = "500", description = "Internal server error during update")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateDeliveryStatusDTO statusDto) {
        try {
            DeliveryRequestDTO updatedDto = deliveryRequestService.updateDeliveryStatus(id, statusDto);
            return ResponseEntity.ok(updatedDto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", (Object)e.getMessage()));
        } catch (IllegalArgumentException e) { // Catch validation errors from DTO
            return ResponseEntity.badRequest().body(Map.of("error", (Object)e.getMessage()));
        } catch (IllegalStateException e) { // Catch invalid state transitions (e.g., from SUCCESSFUL)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", (Object)e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating status for delivery request {}", id, e); // Log unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", (Object)("An unexpected error occurred while updating status: " + e.getMessage())));
        }
    }

    @Operation(summary = "Delete a delivery request",
               description = "Deletes a delivery request only if its status is ASSIGNED or IN_TRANSIT. Updates livreur availability.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Delivery request successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Delivery request not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - Deletion not allowed for the current status (e.g., PENDING, SUCCESSFUL)"),
        @ApiResponse(responseCode = "500", description = "Internal server error during deletion")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeliveryRequest(@PathVariable Long id) {
        try {
            deliveryRequestService.deleteDeliveryRequest(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content for successful deletion
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", (Object)e.getMessage()));
        } catch (IllegalStateException e) { // Catch the specific exception for disallowed status
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", (Object)e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting delivery request {}", id, e); // Log unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", (Object)("An unexpected error occurred while deleting the delivery request: " + e.getMessage())));
        }
    }

    // Endpoint to manually assign a livreur to a delivery request
    @Operation(summary = "Manually assign a livreur to a delivery request",
               description = "Assigns a specific, available livreur to a PENDING delivery request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livreur successfully assigned"),
        @ApiResponse(responseCode = "404", description = "Delivery request or Livreur not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - Delivery request already assigned, not in PENDING state, or Livreur is not available"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{deliveryRequestId}/assign/{livreurId}")
    public ResponseEntity<?> assignLivreurManually(
            @PathVariable Long deliveryRequestId,
            @PathVariable Long livreurId) {
        try {
            DeliveryRequestDTO updatedDto = deliveryRequestService.assignLivreurManually(deliveryRequestId, livreurId);
            return ResponseEntity.ok(updatedDto);
        } catch (ResourceNotFoundException e) {
            log.warn("Manual assignment failed for DR {}: {}", deliveryRequestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DeliveryAlreadyAssignedException | IllegalStateException e) { // Catch both assignment/state conflicts
             log.warn("Manual assignment conflict for DR {}: {}", deliveryRequestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during manual assignment for DR {} and Livreur {}", deliveryRequestId, livreurId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred during manual assignment."));
        }
    }

    @Operation(summary = "Geocode an address",
               description = "Converts a street address into latitude and longitude coordinates using Nominatim (OpenStreetMap).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Geocoding successful, coordinates returned"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Address parameter is missing or empty, or address encoding failed"),
        @ApiResponse(responseCode = "404", description = "Not Found - Address could not be geocoded (no results found)"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error - Geocoding service failed or other unexpected error")
    })
    @GetMapping("/geocode")
    public ResponseEntity<Map<String, Object>> geocodeAddress(@RequestParam String address) {
        log.info("Received request to geocode address: {}", address);
        try {
            Map<String, Double> coordinates = deliveryRequestService.getCoordinatesFromAddress(address);
            // Return coordinates directly in the body
            return ResponseEntity.ok(Map.of("latitude", (Object)coordinates.get("latitude"), 
                                             "longitude", (Object)coordinates.get("longitude")));
        } catch (GeocodingException e) {
            // Handle specific geocoding errors
            log.warn("Geocoding failed for address '{}': {}", address, e.getMessage());
            // Determine appropriate status code based on the exception message/cause if needed
            // For simplicity, map common failure types
            if (e.getMessage().contains("No results found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", (Object)e.getMessage()));
            } else if (e.getMessage().contains("Address cannot be empty") || e.getMessage().contains("Failed to encode address")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", (Object)e.getMessage()));
            } else {
                // Treat other GeocodingExceptions (API errors, missing fields) as internal server errors
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", (Object)e.getMessage()));
            }
        } catch (Exception e) {
            // Catch any other unexpected errors during the process
            log.error("Unexpected error during geocoding for address '{}'", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", (Object)"An unexpected internal error occurred during geocoding."));
        }
    }
}
