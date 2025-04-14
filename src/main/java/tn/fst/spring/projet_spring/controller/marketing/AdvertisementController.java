package tn.fst.spring.projet_spring.controller.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.fst.spring.projet_spring.dto.marketing.AdvertisementDTO;
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
    public ResponseEntity<Object> create(@RequestBody AdvertisementDTO advertisementDTO) {
        Advertisement advertisement = toEntity(advertisementDTO);
        try{
            AdvertisementChannel channel = channelService.getById(advertisementDTO.getChannelId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid channel ID."));
            advertisement.setChannel(channel);
            TargetedAudience audience = audienceService.getById(advertisementDTO.getTargetedAudienceId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid audience ID."));
            advertisement.setTargetedAudience(audience);
            Advertisement savedAdvertisement = service.save(advertisement);
            return ResponseEntity.ok(toDTO(savedAdvertisement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid channel or audience ID.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while creating the advertisement, Please verify if the channel is running");
        }
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
                advertisement.getUrl(),
                advertisement.getDescription(),
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
        advertisement.setUrl(dto.getUrl());
        advertisement.setEndDate(dto.getEndDate());
        advertisement.setDescription(dto.getDescription());
        advertisement.setCost(dto.getCost());
        advertisement.setType(dto.getType());
        advertisement.setViews(dto.getViews());
        advertisement.setInitialViews(dto.getInitialViews());
        return advertisement;
    }
}
