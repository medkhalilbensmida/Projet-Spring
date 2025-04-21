package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.donation.CharityEventRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.CharityEventResponseDTO;
import tn.fst.spring.projet_spring.model.donation.CharityEvent;
import tn.fst.spring.projet_spring.model.donation.Fundraiser;
import tn.fst.spring.projet_spring.repositories.donation.CharityEventRepository;
import tn.fst.spring.projet_spring.repositories.donation.FundraiserRepository;
import tn.fst.spring.projet_spring.services.interfaces.ICharityEventService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharityEventServiceImpl implements ICharityEventService {

    private final CharityEventRepository eventRepository;
    private final FundraiserRepository fundraiserRepository;

    @Override
    public CharityEventResponseDTO createEvent(CharityEventRequestDTO dto) {
        Fundraiser fundraiser = fundraiserRepository.findById(dto.getFundraiserId()).orElseThrow();

        CharityEvent event = new CharityEvent();
        event.setName(dto.getName());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setDescription(dto.getDescription());
        event.setFundraiser(fundraiser);

        CharityEvent saved = eventRepository.save(event);

        return mapToResponseDTO(saved);
    }

    @Override
    public List<CharityEventResponseDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    public CharityEventResponseDTO getEventById(Long id) {
        CharityEvent event = eventRepository.findById(id).orElseThrow();
        return mapToResponseDTO(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
    @Override
    public CharityEventResponseDTO updateEvent(Long id, CharityEventRequestDTO dto) {
        CharityEvent event = eventRepository.findById(id).orElseThrow();
        Fundraiser fundraiser = fundraiserRepository.findById(dto.getFundraiserId()).orElseThrow();

        event.setName(dto.getName());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setDescription(dto.getDescription());
        event.setFundraiser(fundraiser);

        CharityEvent updated = eventRepository.save(event);
        return mapToResponseDTO(updated);
    }

    private CharityEventResponseDTO mapToResponseDTO(CharityEvent event) {
        CharityEventResponseDTO dto = new CharityEventResponseDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setLocation(event.getLocation());
        dto.setEventDate(event.getEventDate());
        dto.setDescription(event.getDescription());
        dto.setFundraiserTitle(event.getFundraiser() != null ? event.getFundraiser().getTitle() : null);
        return dto;
    }
}