package tn.fst.spring.projet_spring.controller.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.controller.marketing.dto.TargetedAudienceDTO;
import tn.fst.spring.projet_spring.entities.marketing.TargetedAudience;
import tn.fst.spring.projet_spring.services.marketing.TargetedAudienceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audiences")
@RequiredArgsConstructor
public class TargetedAudienceController {

    private final TargetedAudienceService service;

    @GetMapping
    public List<TargetedAudienceDTO> getAll() {
        return service.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TargetedAudienceDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TargetedAudienceDTO create(@RequestBody TargetedAudienceDTO audienceDTO) {
        TargetedAudience audience = toEntity(audienceDTO);
        return toDTO(service.save(audience));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TargetedAudienceDTO> update(@PathVariable Long id, @RequestBody TargetedAudienceDTO updatedDTO) {
        return service.getById(id)
                .map(existing -> {
                    TargetedAudience updated = toEntity(updatedDTO);
                    updated.setId(id);
                    return ResponseEntity.ok(toDTO(service.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private TargetedAudienceDTO toDTO(TargetedAudience audience) {
        return new TargetedAudienceDTO(
                audience.getId(),
                audience.getAgeMin(),
                audience.getAgeMax(),
                audience.getLocalisation(),
                audience.getGenre()
        );
    }

    private TargetedAudience toEntity(TargetedAudienceDTO dto) {
        TargetedAudience audience = new TargetedAudience();
        audience.setId(dto.getId());
        audience.setAgeMin(dto.getAgeMin());
        audience.setAgeMax(dto.getAgeMax());
        audience.setLocalisation(dto.getLocation());
        audience.setGenre(dto.getGender());
        return audience;
    }
}
