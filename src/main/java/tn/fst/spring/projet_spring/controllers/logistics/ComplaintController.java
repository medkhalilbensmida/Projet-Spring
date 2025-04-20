package tn.fst.spring.projet_spring.controllers.logistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // For authorization
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.logistics.ComplaintRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.ComplaintResponseDTO;
import tn.fst.spring.projet_spring.dto.logistics.ComplaintUpdateStatusDTO;
import tn.fst.spring.projet_spring.dto.logistics.ResolutionRequestDTO; // Assuming created
import tn.fst.spring.projet_spring.dto.logistics.ResolutionResponseDTO; // Assuming created
import tn.fst.spring.projet_spring.model.logistics.ComplaintStatus;
import tn.fst.spring.projet_spring.services.logistics.IComplaintService;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaint Management", description = "APIs for managing customer complaints")
public class ComplaintController {

    private final IComplaintService complaintService;

    @Operation(summary = "Create a new complaint")
    @PostMapping
    // @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')") // Or just authenticated
    public ResponseEntity<ComplaintResponseDTO> createComplaint(@Valid @RequestBody ComplaintRequestDTO complaintRequest) {
        // Consider getting userId from SecurityContextHolder instead of DTO
        ComplaintResponseDTO createdComplaint = complaintService.createComplaint(complaintRequest);
        return new ResponseEntity<>(createdComplaint, HttpStatus.CREATED);
    }

    @Operation(summary = "Get complaint by ID")
    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or @complaintSecurityService.isOwner(#id, authentication.principal)") // Example complex auth
     @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SERVICE')") // Simplified for now
    public ResponseEntity<ComplaintResponseDTO> getComplaintById(@PathVariable Long id) {
        ComplaintResponseDTO complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }

    @Operation(summary = "Get all complaints (Admin/Manager only)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SERVICE')") // Example roles
    public ResponseEntity<List<ComplaintResponseDTO>> getAllComplaints(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) ComplaintStatus status) {

        List<ComplaintResponseDTO> complaints;
        if (userId != null) {
            complaints = complaintService.getComplaintsByUserId(userId);
        } else if (orderId != null) {
            complaints = complaintService.getComplaintsByOrderId(orderId);
        } else if (status != null) {
            complaints = complaintService.getComplaintsByStatus(status);
        } else {
            complaints = complaintService.getAllComplaints();
        }
        return ResponseEntity.ok(complaints);
    }

    @Operation(summary = "Update complaint status (Admin/Manager only)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SERVICE')")
    public ResponseEntity<ComplaintResponseDTO> updateComplaintStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintUpdateStatusDTO statusUpdate) {
        ComplaintResponseDTO updatedComplaint = complaintService.updateComplaintStatus(id, statusUpdate.getStatus());
        return ResponseEntity.ok(updatedComplaint);
    }

     @Operation(summary = "Assign resolution to a complaint (Admin/Manager only)")
     @PostMapping("/{complaintId}/resolution")
     @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SERVICE')")
     public ResponseEntity<ResolutionResponseDTO> assignResolution(
             @PathVariable Long complaintId,
             @Valid @RequestBody ResolutionRequestDTO resolutionRequest) {
         ResolutionResponseDTO resolution = complaintService.assignResolution(complaintId, resolutionRequest);
         return ResponseEntity.ok(resolution);
     }

      @Operation(summary = "Trigger processing of a resolution (Admin/Manager only)")
      @PostMapping("/resolutions/{resolutionId}/process")
      @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SERVICE')")
      public ResponseEntity<Void> processResolution(@PathVariable Long resolutionId) {
          complaintService.processResolution(resolutionId);
          return ResponseEntity.ok().build();
      }


    @Operation(summary = "Delete a complaint (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
