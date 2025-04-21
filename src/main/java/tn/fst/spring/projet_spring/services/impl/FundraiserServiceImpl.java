package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.donation.FundraiserRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.FundraiserResponseDTO;
import tn.fst.spring.projet_spring.model.donation.Fundraiser;
import tn.fst.spring.projet_spring.repositories.donation.FundraiserRepository;
import tn.fst.spring.projet_spring.services.interfaces.IFundraiserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundraiserServiceImpl implements IFundraiserService {

    private final FundraiserRepository fundraiserRepository;

    @Override
    public FundraiserResponseDTO createFundraiser(FundraiserRequestDTO dto) {
        Fundraiser fundraiser = new Fundraiser();
        fundraiser.setTitle(dto.getTitle());
        fundraiser.setDescription(dto.getDescription());
        fundraiser.setTargetAmount(dto.getTargetAmount());
        fundraiser.setCollectedAmount(0);

        Fundraiser saved = fundraiserRepository.save(fundraiser);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<FundraiserResponseDTO> getAllFundraisers() {
        return fundraiserRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FundraiserResponseDTO getFundraiserById(Long id) {
        Fundraiser fundraiser = fundraiserRepository.findById(id).orElseThrow();
        return mapToResponseDTO(fundraiser);
    }

    @Override
    public FundraiserResponseDTO updateFundraiser(Long id, FundraiserRequestDTO dto) {
        Fundraiser fundraiser = fundraiserRepository.findById(id).orElseThrow();

        fundraiser.setTitle(dto.getTitle());
        fundraiser.setDescription(dto.getDescription());
        fundraiser.setTargetAmount(dto.getTargetAmount());

        Fundraiser updated = fundraiserRepository.save(fundraiser);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteFundraiser(Long id) {
        fundraiserRepository.deleteById(id);
    }

    private FundraiserResponseDTO mapToResponseDTO(Fundraiser fundraiser) {
        FundraiserResponseDTO dto = new FundraiserResponseDTO();
        dto.setId(fundraiser.getId());
        dto.setTitle(fundraiser.getTitle());
        dto.setDescription(fundraiser.getDescription());
        dto.setTargetAmount(fundraiser.getTargetAmount());
        dto.setCollectedAmount(fundraiser.getCollectedAmount());
        return dto;
    }
}