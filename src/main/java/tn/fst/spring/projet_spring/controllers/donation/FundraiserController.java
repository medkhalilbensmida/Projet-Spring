package tn.fst.spring.projet_spring.controllers.donation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.donation.FundraiserRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.FundraiserResponseDTO;
import tn.fst.spring.projet_spring.services.interfaces.IFundraiserService;

import java.util.List;

@RestController
@RequestMapping("/api/fundraisers")
@RequiredArgsConstructor
public class FundraiserController {

    private final IFundraiserService fundraiserService;

    @PostMapping
    public FundraiserResponseDTO create(@RequestBody FundraiserRequestDTO dto) {
        return fundraiserService.createFundraiser(dto);
    }

    @GetMapping
    public List<FundraiserResponseDTO> getAll() {
        return fundraiserService.getAllFundraisers();
    }

    @GetMapping("/{id}")
    public FundraiserResponseDTO getById(@PathVariable Long id) {
        return fundraiserService.getFundraiserById(id);
    }

    @PutMapping("/{id}")
    public FundraiserResponseDTO update(@PathVariable Long id, @RequestBody FundraiserRequestDTO dto) {
        return fundraiserService.updateFundraiser(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        fundraiserService.deleteFundraiser(id);
    }
}