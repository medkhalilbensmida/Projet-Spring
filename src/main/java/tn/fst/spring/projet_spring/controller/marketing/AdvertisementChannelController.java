package tn.fst.spring.projet_spring.controller.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.controller.marketing.dto.AdvertisementChannelDTO;
import tn.fst.spring.projet_spring.entities.marketing.AdvertisementChannel;
import tn.fst.spring.projet_spring.entities.marketing.ChannelType;
import tn.fst.spring.projet_spring.services.marketing.AdvertisementChannelService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class AdvertisementChannelController {

    private final AdvertisementChannelService service;

    @GetMapping
    public List<AdvertisementChannelDTO> getAll() {
        return service.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementChannelDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(channel -> ResponseEntity.ok(toDTO(channel)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AdvertisementChannelDTO create(@RequestBody AdvertisementChannelDTO channelDTO) {
        AdvertisementChannel channel = toEntity(channelDTO);
        return toDTO(service.save(channel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementChannelDTO> update(@PathVariable Long id, @RequestBody AdvertisementChannelDTO updatedDTO) {
        return service.getById(id)
                .map(existing -> {
                    AdvertisementChannel updated = toEntity(updatedDTO);
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

    // Convert AdvertisementChannel entity to AdvertisementChannelDTO
    private AdvertisementChannelDTO toDTO(AdvertisementChannel channel) {
        return new AdvertisementChannelDTO(
                channel.getId(),
                channel.getType().name(),  // ChannelType to String (Enum)
                channel.getPlateforme(),
                channel.getCoutMoyenParVue()
        );
    }

    // Convert AdvertisementChannelDTO to AdvertisementChannel entity
    private AdvertisementChannel toEntity(AdvertisementChannelDTO dto) {
        AdvertisementChannel channel = new AdvertisementChannel();
        channel.setId(dto.getId());
        channel.setType(ChannelType.valueOf(dto.getType()));  // Convert String to Enum
        channel.setPlateforme(dto.getPlatform());
        channel.setCoutMoyenParVue(dto.getAverageCostPerView());
        return channel;
    }
}
