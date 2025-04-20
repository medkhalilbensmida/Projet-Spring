package tn.fst.spring.projet_spring.dto.logistics;

import lombok.Builder;
import lombok.Data;
import tn.fst.spring.projet_spring.model.logistics.ComplaintStatus;
import tn.fst.spring.projet_spring.dto.logistics.ResolutionResponseDTO; // Assuming this DTO exists or will be created

import java.time.LocalDateTime; // Assuming Complaint entity will have timestamps

@Data
@Builder
public class ComplaintResponseDTO {
    private Long id;
    private Long userId;
    private String username; // User who made the complaint
    private Long orderId;
    private String orderNumber; // Associated order number
    private String description;
    private ComplaintStatus status;
    private LocalDateTime createdAt; // Assuming Complaint has createdAt
    private LocalDateTime updatedAt; // Assuming Complaint has updatedAt
    private ResolutionResponseDTO resolution; // Include resolution details if available
}