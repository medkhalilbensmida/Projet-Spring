package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.donation.DonationRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.DonationResponseDTO;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.catalog.Product;
import tn.fst.spring.projet_spring.model.donation.CharityEvent;
import tn.fst.spring.projet_spring.model.donation.Donation;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.donation.CharityEventRepository;
import tn.fst.spring.projet_spring.repositories.donation.DonationRepository;
import tn.fst.spring.projet_spring.repositories.products.ProductRepository;
import tn.fst.spring.projet_spring.services.interfaces.IDonationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements IDonationService {

    private final DonationRepository donationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CharityEventRepository eventRepository;

    @Override
    public DonationResponseDTO createDonation(DonationRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        User donor = userRepository.findById(dto.getDonorId()).orElseThrow();

        Donation donation = new Donation();
        donation.setProduct(product);
        donation.setQuantity(dto.getQuantity());
        donation.setDonor(donor);

        if (dto.getEventId() != null) {
            CharityEvent event = eventRepository.findById(dto.getEventId()).orElseThrow();
            donation.setEvent(event);
        }

        Donation saved = donationRepository.save(donation);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<DonationResponseDTO> getAllDonations() {
        return donationRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DonationResponseDTO getDonationById(Long id) {
        Donation donation = donationRepository.findById(id).orElseThrow();
        return mapToResponseDTO(donation);
    }

    @Override
    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }

    private DonationResponseDTO mapToResponseDTO(Donation donation) {
        DonationResponseDTO dto = new DonationResponseDTO();
        dto.setId(donation.getId());
        dto.setQuantity(donation.getQuantity());
        dto.setProductName(donation.getProduct().getName());
        dto.setDonorFullName(donation.getDonor().getUsername());
        dto.setEventName(donation.getEvent() != null ? donation.getEvent().getName() : null);
        return dto;
    }
}
