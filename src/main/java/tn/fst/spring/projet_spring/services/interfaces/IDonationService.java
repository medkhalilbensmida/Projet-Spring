package tn.fst.spring.projet_spring.services.interfaces;


import tn.fst.spring.projet_spring.dto.donation.DonationRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.DonationResponseDTO;

import java.util.List;

public interface IDonationService {
    tn.fst.spring.projet_spring.dto.donation.DonationResponseDTO createDonation(DonationRequestDTO dto);
    List<DonationResponseDTO> getAllDonations();
    DonationResponseDTO getDonationById(Long id);
    void deleteDonation(Long id);
}
