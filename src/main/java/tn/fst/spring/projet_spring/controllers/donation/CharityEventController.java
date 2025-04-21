package tn.fst.spring.projet_spring.controllers.donation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.donation.CharityEventRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.CharityEventResponseDTO;
import tn.fst.spring.projet_spring.services.impl.CharityEventServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class CharityEventController {

    private final CharityEventServiceImpl charityEventService;

    @PostMapping
    public CharityEventResponseDTO createEvent(@RequestBody CharityEventRequestDTO dto) {
        return charityEventService.createEvent(dto);
    }

    @GetMapping
    public List<CharityEventResponseDTO> getAllEvents() {
        return charityEventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public CharityEventResponseDTO getEvent(@PathVariable Long id) {
        return charityEventService.getEventById(id);
    }

    @PutMapping("/{id}")
    public CharityEventResponseDTO updateEvent(@PathVariable Long id, @RequestBody CharityEventRequestDTO dto) {
        return charityEventService.updateEvent(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        charityEventService.deleteEvent(id);
    }
}