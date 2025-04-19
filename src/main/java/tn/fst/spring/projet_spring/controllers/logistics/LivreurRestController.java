package tn.fst.spring.projet_spring.controllers.logistics;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.fst.spring.projet_spring.dto.logistics.CreateLivreurRequest;
import tn.fst.spring.projet_spring.dto.logistics.DeliveryRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.LivreurStatsDTO;
import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurAvailabilityRequest;
import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.services.logistics.ILivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/livreurs")
@Tag(name = "Livreur Management", description = "APIs for managing livreurs")
public class LivreurRestController {

    ILivreurService livreurService;

    @Operation(summary = "Retrieve all livreurs", description = "Gets a list of all available livreurs.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping
    public List<Livreur> getAllLivreurs() {
        return livreurService.retrieveAllLivreurs();
    }

    @Operation(summary = "Get a livreur by ID", description = "Retrieves a specific livreur by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved livreur"),
            @ApiResponse(responseCode = "404", description = "Livreur not found")
    })
    @GetMapping("/{id}")
    public Livreur getLivreurById(@PathVariable Long id) {
        return livreurService.retrieveLivreur(id);
    }

    @Operation(summary = "Create a new livreur", description = "Adds a new livreur to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livreur created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Livreur> createLivreur(@Valid @RequestBody CreateLivreurRequest livreurRequest) {
        // Map DTO to Entity
        Livreur livreurToCreate = new Livreur();
        livreurToCreate.setNom(livreurRequest.getNom());
        livreurToCreate.setDisponible(livreurRequest.getDisponible());

        Livreur createdLivreur = livreurService.addLivreur(livreurToCreate);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdLivreur.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdLivreur);
    }

    @Operation(summary = "Update an existing livreur", description = "Updates the details of an existing livreur by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or ID mismatch"),
            @ApiResponse(responseCode = "404", description = "Livreur not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Livreur> updateLivreur(@PathVariable Long id, @Valid @RequestBody UpdateLivreurRequest livreurRequest) {
        Livreur updatedLivreur = livreurService.updateLivreur(id, livreurRequest);
        if (updatedLivreur == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedLivreur);
    }

    @Operation(summary = "Update livreur availability", description = "Updates the availability status of a specific livreur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., null availability)"),
            @ApiResponse(responseCode = "404", description = "Livreur not found")
    })
    @PatchMapping("/{id}/disponibilite")
    public ResponseEntity<Livreur> updateLivreurAvailability(@PathVariable Long id, @Valid @RequestBody UpdateLivreurAvailabilityRequest availabilityRequest) {
        Livreur updatedLivreur = livreurService.updateLivreurAvailability(id, availabilityRequest.getDisponible());
        if (updatedLivreur == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedLivreur);
    }

    @Operation(summary = "Delete a livreur", description = "Removes a livreur from the system by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Livreur deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Livreur not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivreur(@PathVariable Long id) {
        livreurService.removeLivreur(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Calculate livreur prime (bonus)", description = "Calculates the prime (bonus) for a specific livreur based on their completed deliveries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated prime"),
            @ApiResponse(responseCode = "404", description = "Livreur not found")
    })
    @GetMapping("/{id}/prime")
    public ResponseEntity<Double> getLivreurPrime(@PathVariable Long id) {
        try {
            double prime = livreurService.calculatePrime(id);
            return ResponseEntity.ok(prime);
        } catch (RuntimeException e) {
            // Assuming the service throws RuntimeException for not found livreur
            // A more specific exception handling might be needed
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get assigned deliveries for a livreur", description = "Retrieves a list of deliveries assigned to a specific livreur.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved assigned deliveries")
    @GetMapping("/{id}/deliveries")
    public ResponseEntity<List<DeliveryRequestDTO>> getAssignedDeliveries(@PathVariable Long id) {
        List<DeliveryRequest> deliveries = livreurService.getAssignedDeliveries(id);
        List<DeliveryRequestDTO> dtos = deliveries.stream()
                .map(dr -> new DeliveryRequestDTO(
                        dr.getId(),
                        dr.getDeliveryFee(),
                        dr.getStatus(),
                        dr.getOrder().getId(),
                        dr.getDestinationLat(), // Add destination lat
                        dr.getDestinationLon()  // Add destination lon
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get livreur of the month", description = "Finds the livreur with the most delivered deliveries in the current month.")
    @ApiResponse(responseCode = "200", description = "Successfully found livreur of the month")
    @GetMapping("/of-month")
    public ResponseEntity<Livreur> getLivreurOfMonth() {
        Livreur top = livreurService.getLivreurOfMonth();
        if (top == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(top);
    }

    @Operation(summary = "Get livreurs stats", description = "Percentage distribution of statuses and total delivered this month per livreur")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stats")
    @GetMapping("/stats")
    public ResponseEntity<List<LivreurStatsDTO>> getLivreurStats() {
        return ResponseEntity.ok(livreurService.getLivreurStats());
    }
}