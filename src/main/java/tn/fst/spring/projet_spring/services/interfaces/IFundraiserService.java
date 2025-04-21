package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.donation.FundraiserRequestDTO;
import tn.fst.spring.projet_spring.dto.donation.FundraiserResponseDTO;

import java.util.List;

public interface IFundraiserService {
    FundraiserResponseDTO createFundraiser(FundraiserRequestDTO dto);
    List<FundraiserResponseDTO> getAllFundraisers();
    FundraiserResponseDTO getFundraiserById(Long id);
    FundraiserResponseDTO updateFundraiser(Long id, FundraiserRequestDTO dto);
    void deleteFundraiser(Long id);
}
