package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.donation.CharityEventRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.CharityEventResponseDTO;

import java.util.List;

public interface ICharityEventService {
    public CharityEventResponseDTO createEvent(CharityEventRequestDTO request);
    public List<CharityEventResponseDTO> getAllEvents();
    public CharityEventResponseDTO getEventById(Long id);
    public void deleteEvent(Long id);
    public CharityEventResponseDTO updateEvent(Long id, CharityEventRequestDTO dto);
}
