package tn.fst.spring.projet_spring.services.logistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.fst.spring.projet_spring.dto.logistics.*;
import tn.fst.spring.projet_spring.exception.ResourceNotFoundException;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.logistics.Complaint;
import tn.fst.spring.projet_spring.model.logistics.ComplaintStatus;
import tn.fst.spring.projet_spring.model.logistics.Resolution;
import tn.fst.spring.projet_spring.model.logistics.ResolutionStatus;
import tn.fst.spring.projet_spring.model.logistics.ResolutionType;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.logistics.ComplaintRepository;
import tn.fst.spring.projet_spring.repositories.logistics.ResolutionRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;
import tn.fst.spring.projet_spring.services.interfaces.IOrderService;
import tn.fst.spring.projet_spring.services.interfaces.IPaymentService;
import tn.fst.spring.projet_spring.services.interfaces.IProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintServiceImpl implements IComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ResolutionRepository resolutionRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final IPaymentService paymentService;
    private final IOrderService orderService;
    private final IProductService productService;

    @Override
    @Transactional
    public ComplaintResponseDTO createComplaint(ComplaintRequestDTO complaintRequest) {
        log.info("Creating complaint for order ID: {}", complaintRequest.getOrderId());
        User user = userRepository.findById(complaintRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + complaintRequest.getUserId()));
        Order order = orderRepository.findById(complaintRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + complaintRequest.getOrderId()));

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setOrder(order);
        complaint.setDescription(complaintRequest.getDescription());
        complaint.setStatus(ComplaintStatus.OPEN);

        Complaint savedComplaint = complaintRepository.save(complaint);
        log.info("Complaint created successfully with ID: {}", savedComplaint.getId());
        return convertToResponse(savedComplaint);
    }

    @Override
    public ComplaintResponseDTO getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        return convertToResponse(complaint);
    }

    @Override
    public List<ComplaintResponseDTO> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsByUserId(Long userId) {
         if (!userRepository.existsById(userId)) {
             throw new ResourceNotFoundException("User not found with id: " + userId);
         }
        return complaintRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsByOrderId(Long orderId) {
         if (!orderRepository.existsById(orderId)) {
             throw new ResourceNotFoundException("Order not found with id: " + orderId);
         }
        return complaintRepository.findByOrderId(orderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComplaintResponseDTO updateComplaintStatus(Long id, ComplaintStatus newStatus) {
        log.info("Updating status for complaint ID: {} to {}", id, newStatus);
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        complaint.setStatus(newStatus);
        Complaint updatedComplaint = complaintRepository.save(complaint);
        log.info("Complaint status updated successfully for ID: {}", id);
        return convertToResponse(updatedComplaint);
    }

    @Override
    @Transactional
    public void deleteComplaint(Long id) {
        log.warn("Attempting to delete complaint ID: {}", id);
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        complaintRepository.delete(complaint);
        log.info("Complaint deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public ResolutionResponseDTO assignResolution(Long complaintId, ResolutionRequestDTO resolutionRequest) {
        log.info("Assigning resolution type {} to complaint ID: {}", resolutionRequest.getType(), complaintId);
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));
        if (complaint.getResolution() != null) {
            throw new IllegalStateException("Complaint " + complaintId + " already has a resolution assigned.");
        }
        if (complaint.getStatus() == ComplaintStatus.RESOLVED || complaint.getStatus() == ComplaintStatus.REJECTED) {
             throw new IllegalStateException("Cannot assign resolution to a complaint that is already " + complaint.getStatus());
        }
        Resolution resolution = new Resolution();
        resolution.setComplaint(complaint);
        resolution.setType(resolutionRequest.getType());
        resolution.setStatus(resolutionRequest.getStatus());
        Resolution savedResolution = resolutionRepository.save(resolution);
        complaint.setResolution(savedResolution);
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        complaintRepository.save(complaint);
        log.info("Resolution ID: {} assigned successfully to complaint ID: {}", savedResolution.getId(), complaintId);
        return convertToResolutionResponse(savedResolution);
    }

    @Override
    @Transactional
    public void processResolution(Long resolutionId) {
        log.info("Processing resolution ID: {}", resolutionId);
        Resolution resolution = resolutionRepository.findById(resolutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resolution not found with id: " + resolutionId));
        Complaint complaint = resolution.getComplaint();
        if (complaint == null) {
             throw new IllegalStateException("Resolution " + resolutionId + " is not linked to any complaint.");
        }
        Order order = complaint.getOrder();
        if (order == null) {
             throw new IllegalStateException("Complaint " + complaint.getId() + " is not linked to any order.");
        }
        if (resolution.getStatus() != ResolutionStatus.APPROVED) {
            log.warn("Resolution {} is not in APPROVED state (current: {}). Skipping processing.", resolutionId, resolution.getStatus());
            return;
        }
        try {
            switch (resolution.getType()) {
                case REFUND:
                    log.info("Processing REFUND for resolution ID: {}, order ID: {}", resolutionId, order.getId());
                    // --- Actual refund logic ---
                    Double refundAmount = order.getTotalAmount(); // Example: refund full amount
                    String refundReason = "Refund approved for complaint ID: " + complaint.getId();
                    paymentService.initiateRefund(order.getId(), refundAmount, refundReason);
                    resolution.setStatus(ResolutionStatus.IMPLEMENTED);
                    break;
                
                case REPLACEMENT:
                    log.info("Processing REPAIR for resolution ID: {}, order ID: {}", resolutionId, order.getId());
                    log.warn("Placeholder: Repair tracking logic for resolution {} needs implementation.", resolutionId);
                    resolution.setStatus(ResolutionStatus.IMPLEMENTED);
                    break;
                default:
                    log.error("Unknown resolution type: {} for resolution ID: {}", resolution.getType(), resolutionId);
                    throw new IllegalStateException("Unsupported resolution type encountered.");
            }
            resolutionRepository.save(resolution);
            complaint.setStatus(ComplaintStatus.RESOLVED);
            complaintRepository.save(complaint);
            log.info("Resolution ID: {} processed successfully. Complaint ID: {} marked as RESOLVED.", resolutionId, complaint.getId());
        } catch (Exception e) {
            log.error("Error processing resolution ID: {}. Error: {}", resolutionId, e.getMessage(), e);
            resolution.setStatus(ResolutionStatus.REJECTED);
            resolutionRepository.save(resolution);
            throw new RuntimeException("Failed to process resolution " + resolutionId, e);
        }
    }

    // --- Helper Methods ---
    private ComplaintResponseDTO convertToResponse(Complaint complaint) {
        ResolutionResponseDTO resolutionDTO = null;
        if (complaint.getResolution() != null) {
            resolutionDTO = convertToResolutionResponse(complaint.getResolution());
        }
        String username = (complaint.getUser() != null) ? complaint.getUser().getUsername() : "N/A";
        Long userId = (complaint.getUser() != null) ? complaint.getUser().getId() : null;
        String orderNumber = (complaint.getOrder() != null) ? complaint.getOrder().getOrderNumber() : "N/A";
        Long orderId = (complaint.getOrder() != null) ? complaint.getOrder().getId() : null;
        return ComplaintResponseDTO.builder()
                .id(complaint.getId())
                .userId(userId)
                .username(username)
                .orderId(orderId)
                .orderNumber(orderNumber)
                .description(complaint.getDescription())
                .status(complaint.getStatus())
                .resolution(resolutionDTO)
                .build();
    }

    private ResolutionResponseDTO convertToResolutionResponse(Resolution resolution) {
        if (resolution == null) {
            return null;
        }
        Long complaintId = (resolution.getComplaint() != null) ? resolution.getComplaint().getId() : null;
        return ResolutionResponseDTO.builder()
                .id(resolution.getId())
                .type(resolution.getType())
                .status(resolution.getStatus())
                .complaintId(complaintId)
                .build();
    }
}
