package tn.fst.spring.projet_spring.controllers.donation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.donation.DonationRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.DonationResponseDTO;
import tn.fst.spring.projet_spring.services.interfaces.IDonationService;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final IDonationService donationService;

    @PostMapping
    public DonationResponseDTO create(@RequestBody DonationRequestDTO dto) {
        return donationService.createDonation(dto);
    }

    @GetMapping
    public List<DonationResponseDTO> getAll() {
        return donationService.getAllDonations();
    }

    @GetMapping("/{id}")
    public DonationResponseDTO getById(@PathVariable Long id) {
        return donationService.getDonationById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        donationService.deleteDonation(id);
    }
}