package tn.fst.spring.projet_spring.controllers.logistics;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.fst.spring.projet_spring.dto.logistics.CreateLivreurRequest;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.services.logistics.ILivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<Livreur> updateLivreur(@PathVariable Long id, @RequestBody Livreur livreur) {
        if (livreur.getId() == null) {
            livreur.setId(id);
        } else if (!livreur.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        Livreur updatedLivreur = livreurService.updateLivreur(livreur);
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
} 