package tn.fst.spring.projet_spring.controller.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.controller.marketing.dto.AdvertisementDTO;
import tn.fst.spring.projet_spring.entities.marketing.Advertisement;
import tn.fst.spring.projet_spring.entities.marketing.AdvertisementChannel;
import tn.fst.spring.projet_spring.entities.marketing.TargetedAudience;
import tn.fst.spring.projet_spring.services.marketing.AdvertisementChannelService;
import tn.fst.spring.projet_spring.services.marketing.AdvertisementService;
import tn.fst.spring.projet_spring.services.marketing.TargetedAudienceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService service;
    private final AdvertisementChannelService channelService;
    private final TargetedAudienceService audienceService;

    @GetMapping
    public List<AdvertisementDTO> getAll() {
        return service.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ad -> ResponseEntity.ok(toDTO(ad)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AdvertisementDTO create(@RequestBody AdvertisementDTO advertisementDTO) {
        Advertisement advertisement = toEntity(advertisementDTO);
        return toDTO(service.save(advertisement));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> update(@PathVariable Long id, @RequestBody AdvertisementDTO updatedDTO) {
        return service.getById(id)
                .map(existing -> {
                    Advertisement updated = toEntity(updatedDTO);
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

    private AdvertisementDTO toDTO(Advertisement advertisement) {
        return new AdvertisementDTO(
                advertisement.getId(),
                advertisement.getName(),
                advertisement.getChannel().getId(),
                advertisement.getTargetedAudience().getId(),
                advertisement.getStartDate(),
                advertisement.getEndDate(),
                advertisement.getCost(),
                advertisement.getType(),
                advertisement.getViews(),
                advertisement.getInitialViews()
        );
    }

    private Advertisement toEntity(AdvertisementDTO dto) {
        Advertisement advertisement = new Advertisement();
        advertisement.setId(dto.getId());
        advertisement.setName(dto.getName());

        // Fetch and set the actual AdvertisementChannel object using the ID from DTO
        Optional<AdvertisementChannel> channel = channelService.getById(dto.getChannelId());
        if (channel.isPresent()) {
            advertisement.setChannel(channel.get());
        } else {
            throw new IllegalArgumentException("Invalid channel ID.");
        }

        // Fetch and set the actual TargetedAudience object using the ID from DTO
        Optional<TargetedAudience> audience = audienceService.getById(dto.getTargetedAudienceId());
        if (audience.isPresent()) {
            advertisement.setTargetedAudience(audience.get());
        } else {
            throw new IllegalArgumentException("Invalid targeted audience ID.");
        }

        advertisement.setStartDate(dto.getStartDate());
        advertisement.setEndDate(dto.getEndDate());
        advertisement.setCost(dto.getCost());
        advertisement.setType(dto.getType());
        advertisement.setViews(dto.getViews());
        advertisement.setInitialViews(dto.getInitialViews());
        return advertisement;
    }
}
