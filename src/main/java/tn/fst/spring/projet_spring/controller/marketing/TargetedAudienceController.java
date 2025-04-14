package tn.fst.spring.projet_spring.controller.marketing;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.fst.spring.projet_spring.dto.marketing.TargetedAudienceDTO;
import tn.fst.spring.projet_spring.entities.marketing.TargetedAudience;
import tn.fst.spring.projet_spring.services.marketing.TargetedAudienceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audiences")
@RequiredArgsConstructor
public class TargetedAudienceController {

    private final TargetedAudienceService service;

    @Operation(summary = "Get all targeted audiences", description = "Fetch all targeted audiences")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of audiences",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = TargetedAudienceDTO.class)))
    })
    @GetMapping
    public List<TargetedAudienceDTO> getAll() {
        return service.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get targeted audience by ID", description = "Fetch a targeted audience by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully fetched the audience",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = TargetedAudienceDTO.class))),
        @ApiResponse(responseCode = "404", description = "Targeted audience not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TargetedAudienceDTO> getById(@Parameter(description = "ID of the targeted audience to be fetched") @PathVariable Long id) {
        return service.getById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new targeted audience", description = "Create a new targeted audience with provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the targeted audience",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = TargetedAudienceDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public TargetedAudienceDTO create(@RequestBody @Schema(description = "Targeted Audience object to be created", 
                                                        example = "{\n  \"id\": 1,\n  \"name\": \"Young Adults Tunisia\",\n  \"ageMin\": 18,\n  \"ageMax\": 30,\n  \"location\": \"Tunis\",\n  \"gender\": \"Mixed\"\n}") TargetedAudienceDTO audienceDTO) {
        TargetedAudience audience = toEntity(audienceDTO);
        return toDTO(service.save(audience));
    }

    @Operation(summary = "Update an existing targeted audience", description = "Update the details of an existing targeted audience")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the targeted audience",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = TargetedAudienceDTO.class))),
        @ApiResponse(responseCode = "404", description = "Targeted audience not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TargetedAudienceDTO> update(@Parameter(description = "ID of the targeted audience to be updated") @PathVariable Long id, 
                                                      @RequestBody @Schema(description = "Updated Targeted Audience object", 
                                                      example = "{\n  \"name\": \"Young Adults Tunisia\",\n  \"ageMin\": 18,\n  \"ageMax\": 30,\n  \"location\": \"Tunis\",\n  \"gender\": \"Mixed\"\n}") TargetedAudienceDTO updatedDTO) {
        return service.getById(id)
                .map(existing -> {
                    TargetedAudience updated = toEntity(updatedDTO);
                    updated.setId(id);
                    return ResponseEntity.ok(toDTO(service.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete targeted audience", description = "Delete the targeted audience by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the targeted audience"),
        @ApiResponse(responseCode = "404", description = "Targeted audience not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID of the targeted audience to be deleted") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private TargetedAudienceDTO toDTO(TargetedAudience audience) {
        return new TargetedAudienceDTO(
                audience.getId(),
                audience.getNom(),
                audience.getAgeMin(),
                audience.getAgeMax(),
                audience.getLocalisation(),
                audience.getGenre()
        );
    }

    private TargetedAudience toEntity(TargetedAudienceDTO dto) {
        TargetedAudience audience = new TargetedAudience();
        audience.setId(dto.getId());
        audience.setNom(dto.getName());
        audience.setAgeMin(dto.getAgeMin());
        audience.setAgeMax(dto.getAgeMax());
        audience.setLocalisation(dto.getLocation());
        audience.setGenre(dto.getGender());
        return audience;
    }
}
