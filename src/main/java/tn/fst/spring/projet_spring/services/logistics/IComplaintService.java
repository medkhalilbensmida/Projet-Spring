package tn.fst.spring.projet_spring.services.logistics;

import tn.fst.spring.projet_spring.dto.logistics.ComplaintRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.ComplaintResponseDTO;
import tn.fst.spring.projet_spring.dto.logistics.ComplaintUpdateStatusDTO;
import tn.fst.spring.projet_spring.dto.logistics.ResolutionRequestDTO; // Assuming this DTO exists or will be created
import tn.fst.spring.projet_spring.dto.logistics.ResolutionResponseDTO; // Assuming this DTO exists or will be created
import tn.fst.spring.projet_spring.model.logistics.ComplaintStatus;

import java.util.List;

public interface IComplaintService {

    ComplaintResponseDTO createComplaint(ComplaintRequestDTO complaintRequest);

    ComplaintResponseDTO getComplaintById(Long id);

    List<ComplaintResponseDTO> getAllComplaints();

    List<ComplaintResponseDTO> getComplaintsByUserId(Long userId);

    List<ComplaintResponseDTO> getComplaintsByOrderId(Long orderId);

    List<ComplaintResponseDTO> getComplaintsByStatus(ComplaintStatus status);

    ComplaintResponseDTO updateComplaintStatus(Long id, ComplaintStatus newStatus);

    void deleteComplaint(Long id);

    // Method for Admin to assign a resolution
    ResolutionResponseDTO assignResolution(Long complaintId, ResolutionRequestDTO resolutionRequest);

    // Method for Admin to approve a pending resolution
    ResolutionResponseDTO approveResolution(Long resolutionId);

    // Method for Admin to reject a pending resolution
    ResolutionResponseDTO rejectResolution(Long resolutionId);

    // Method to initiate the processing of the resolution
    void processResolution(Long resolutionId);
}
