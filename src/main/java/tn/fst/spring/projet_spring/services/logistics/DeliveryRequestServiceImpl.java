package tn.fst.spring.projet_spring.services.logistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.fst.spring.projet_spring.config.ShippingProperties;
import tn.fst.spring.projet_spring.dto.logistics.CreateDeliveryRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.DeliveryRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.UpdateDeliveryDestinationDTO;
import tn.fst.spring.projet_spring.dto.logistics.UpdateDeliveryStatusDTO;
import tn.fst.spring.projet_spring.exception.DeliveryAlreadyAssignedException;
import tn.fst.spring.projet_spring.exception.ResourceNotFoundException;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.repositories.logistics.DeliveryRequestRepository;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryRequestServiceImpl implements IDeliveryRequestService {

    private final DeliveryRequestRepository deliveryRequestRepository;
    private final OrderRepository orderRepository;
    private final LivreurRepository livreurRepository;
    private final ShippingProperties shippingProperties;
    private final DistanceCalculationService distanceCalculationService;

    @Override
    @Transactional
    public DeliveryRequestDTO create(CreateDeliveryRequestDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        // Check if a delivery request already exists for this order
        if (deliveryRequestRepository.findByOrder(order).isPresent()) {
            throw new IllegalStateException("Delivery request already exists for order " + order.getOrderNumber());
        }

        Livreur assignedLivreur = null;
        DeliveryStatus status;

        // Check if a specific livreur is requested
        if (dto.getLivreurId() != null) {
            // Livreur ID is provided, attempt assignment
            Livreur livreur = livreurRepository.findById(dto.getLivreurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Livreur not found with id: " + dto.getLivreurId()));

            // Check if the requested livreur is available
            if (!livreur.isDisponible()) {
                 // You might want to create a more specific exception like LivreurNotAvailableException
                 throw new IllegalStateException("Livreur with id " + dto.getLivreurId() + " is not available.");
            }

            // Assign the livreur and mark them as unavailable
            assignedLivreur = livreur;
            assignedLivreur.setDisponible(false);
            livreurRepository.save(assignedLivreur); // Persist the change in livreur availability
            status = DeliveryStatus.ASSIGNED;
            log.info("Assigned available livreur {} to new delivery request for order {}", assignedLivreur.getId(), order.getOrderNumber());

        } else {
            // No livreur ID provided, set status to PENDING for later assignment
            status = DeliveryStatus.PENDING;
            log.info("No livreur ID provided for order {}. Setting status to PENDING.", order.getOrderNumber());
        }

        // Calculate weight and delivery fee regardless of assignment status
        double totalWeight = order.getItems().stream()
                .mapToDouble(item -> (item.getProduct() != null ? item.getProduct().getWeight() : 0.0) * item.getQuantity())
                .sum();

        double fee = calculateDeliveryFee(dto.getDestinationLat(), dto.getDestinationLon(), totalWeight);

        // Create and populate the new DeliveryRequest entity
        DeliveryRequest dr = new DeliveryRequest();
        dr.setOrder(order);
        dr.setLivreur(assignedLivreur); // Will be null if status is PENDING
        dr.setStatus(status);
        dr.setDeliveryFee(fee);
        dr.setDestinationLat(dto.getDestinationLat());
        dr.setDestinationLon(dto.getDestinationLon());

        // Save the new delivery request
        DeliveryRequest saved = deliveryRequestRepository.save(dr);
        log.info("Successfully created DeliveryRequest {} with status {} for order {}", saved.getId(), saved.getStatus(), order.getOrderNumber());

        // Map the saved entity to a DTO for the response
        return new DeliveryRequestDTO(
                saved.getId(),
                saved.getDeliveryFee(),
                saved.getStatus(),
                saved.getOrder().getId(),
                saved.getLivreur() != null ? saved.getLivreur().getId() : null,
                saved.getDestinationLat(),
                saved.getDestinationLon()
        );
    }

    @Override
    public double calculateDeliveryFee(double destinationLat, double destinationLon, double totalWeightKg) {
        // Input validation
        if (totalWeightKg < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        if (Math.abs(destinationLat) > 90 || Math.abs(destinationLon) > 180) {
            throw new IllegalArgumentException("Invalid coordinates provided.");
        }

        double originLat = shippingProperties.getOriginLat();
        double originLon = shippingProperties.getOriginLon();

        // Use the existing haversine method
        double distance = haversine(originLat, originLon, destinationLat, destinationLon);

        // Calculate fee using injected properties
        double fee = shippingProperties.getFixedCost() +
                (totalWeightKg * shippingProperties.getPricePerKg()) +
                (distance * shippingProperties.getPricePerKm());

        return Math.max(0, fee); // Ensure fee is not negative
    }

    @Override
    public double calculateFeeForRequest(Long deliveryRequestId) {
        // 1. Fetch the DeliveryRequest
        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(deliveryRequestId)
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException("DeliveryRequest not found with ID: " + deliveryRequestId);
                });

        // 2. Get the associated Order
        Order order = deliveryRequest.getOrder();
        if (order == null) {
            throw new ResourceNotFoundException("Order associated with DeliveryRequest ID " + deliveryRequestId + " is null");
        }

        // 3. Get destination coordinates
        Double destinationLat = deliveryRequest.getDestinationLat();
        Double destinationLon = deliveryRequest.getDestinationLon();

        // Ensure coordinates are present
        if (destinationLat == null || destinationLon == null) {
            throw new ResourceNotFoundException("Destination coordinates missing for DeliveryRequest ID: " + deliveryRequestId);
        }

        // 4. Calculate total weight from OrderItems
        double totalWeightKg = order.getItems().stream()
                .mapToDouble(item -> (item.getProduct() != null ? item.getProduct().getWeight() : 0.0) * item.getQuantity())
                .sum();

        // 5. Call the core calculation logic (using the existing private helper)
        return calculateFee(destinationLat, destinationLon, totalWeightKg, shippingProperties);
    }

    @Override
    @Transactional
    public DeliveryRequestDTO autoAssignLivreur(Long deliveryRequestId) {
        // 1. Get the delivery request by ID
        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(deliveryRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery request not found with id: " + deliveryRequestId));

        // 2. Check if request already has an assigned livreur
        if (deliveryRequest.getLivreur() != null) {
            String message = String.format("Delivery request %d already has livreur assigned (ID: %d)", 
                                          deliveryRequestId, deliveryRequest.getLivreur().getId());
            log.info(message);
            // Throw exception instead of returning DTO
            throw new DeliveryAlreadyAssignedException(message);
        }

        // 3. Get origin coordinates from properties
        double originLat = shippingProperties.getOriginLat();
        double originLon = shippingProperties.getOriginLon();

        // 4. Find all available livreurs
        List<Livreur> availableLivreurs = livreurRepository.findByDisponible(true);
        if (availableLivreurs.isEmpty()) {
            throw new ResourceNotFoundException("No available livreurs found");
        }

        // 5. Find the closest livreur to the origin using the DistanceCalculationService
        Optional<Livreur> closestLivreur = availableLivreurs.stream()
            .filter(livreur -> livreur.getLatitude() != null && livreur.getLongitude() != null)
            .min(Comparator.comparingDouble(livreur -> {
                try {
                    // Calculate distance from livreur's current location to the origin
                    double distance = distanceCalculationService.calculateDistance(
                        livreur, originLat, originLon);
                    // --- DEBUG LOG ---
                    log.info("Debugging autoAssignLivreur: Livreur ID: {}, Calculated Distance: {}", livreur.getId(), distance); 
                    // --- END DEBUG LOG ---
                    return distance;
                } catch (Exception e) {
                    log.error("Error calculating distance for livreur {}: {}",
                        livreur.getId(), e.getMessage());
                    return Double.MAX_VALUE; // Effectively exclude this livreur from consideration
                }
            }));

        if (closestLivreur.isEmpty()) {
            // Check if the issue was lack of coordinates or other filtering
             if (availableLivreurs.stream().noneMatch(l -> l.getLatitude() != null && l.getLongitude() != null)) {
                throw new ResourceNotFoundException("No available livreurs with valid coordinates found");
             } else {
                // This case might occur if distance calculation failed for all valid livreurs
                throw new RuntimeException("Could not determine the closest livreur due to calculation errors.");
             }
        }

        // 6. Assign the closest livreur to the delivery request
        Livreur bestLivreur = closestLivreur.get();
        deliveryRequest.setLivreur(bestLivreur);
        deliveryRequest.setStatus(DeliveryStatus.ASSIGNED);
        
        // 7. Mark the livreur as unavailable
        bestLivreur.setDisponible(false);
        livreurRepository.save(bestLivreur);

        // 8. Save the updated delivery request
        DeliveryRequest saved = deliveryRequestRepository.save(deliveryRequest);
        log.info("Auto-assigned livreur {} to delivery request {}", bestLivreur.getId(), deliveryRequestId);

        // 9. Return the updated delivery request DTO
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public DeliveryRequestDTO updateDestinationAndRecalculateFee(Long deliveryRequestId, UpdateDeliveryDestinationDTO dto) {
        // 1. Fetch the DeliveryRequest
        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(deliveryRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with ID: " + deliveryRequestId));

        // You might want to add a check here to prevent updating if the delivery is already in progress or completed
        // e.g., if (!deliveryRequest.getStatus().equals(DeliveryStatus.PENDING) && !deliveryRequest.getStatus().equals(DeliveryStatus.ASSIGNED)) {
        //     throw new IllegalStateException("Cannot update destination for a delivery that is already " + deliveryRequest.getStatus());
        // }

        // 2. Update destination coordinates
        deliveryRequest.setDestinationLat(dto.getDestinationLat());
        deliveryRequest.setDestinationLon(dto.getDestinationLon());

        // 3. Recalculate the fee
        // Get the associated Order to calculate weight
        Order order = deliveryRequest.getOrder();
        if (order == null) {
            // This shouldn't happen based on current logic, but good practice to check
            throw new IllegalStateException("Order associated with DeliveryRequest ID " + deliveryRequestId + " is null");
        }
        double totalWeightKg = order.getItems().stream()
                .mapToDouble(item -> (item.getProduct() != null ? item.getProduct().getWeight() : 0.0) * item.getQuantity())
                .sum();

        // Use the core calculation logic
        double newFee = calculateFee(dto.getDestinationLat(), dto.getDestinationLon(), totalWeightKg, shippingProperties);
        deliveryRequest.setDeliveryFee(newFee);

        // 4. Save the updated DeliveryRequest
        DeliveryRequest updatedRequest = deliveryRequestRepository.save(deliveryRequest);
        log.info("Updated destination for DeliveryRequest {} to [{}, {}] and recalculated fee to {}", 
                 updatedRequest.getId(), dto.getDestinationLat(), dto.getDestinationLon(), newFee);

        // 5. Map and return DTO
        return mapToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public DeliveryRequestDTO updateDeliveryStatus(Long deliveryRequestId, UpdateDeliveryStatusDTO dto) {
        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(deliveryRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with ID: " + deliveryRequestId));

        DeliveryStatus currentStatus = deliveryRequest.getStatus();
        DeliveryStatus newStatus = dto.getNewStatus();

        if (currentStatus == newStatus) {
            log.info("DeliveryRequest {} already has status {}. No change needed.", deliveryRequestId, newStatus);
            return mapToDTO(deliveryRequest); // No change, return current state
        }

        // Rule: DO NOT ALLOW any change from SUCCESSFUL
        if (currentStatus == DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Cannot change status of a successfully completed delivery request (ID: " + deliveryRequestId + ")");
        }

        // Rule: PENDING can only transition to ASSIGNED
        if (currentStatus == DeliveryStatus.PENDING) {
            throw new IllegalStateException("A PENDING delivery request (ID: " + deliveryRequestId + ") cannot be changed (need to be assigned a livreur).");
        }

        // Rule: Handle specific transitions affecting Livreur
        Livreur livreur = deliveryRequest.getLivreur();
        boolean livreurNeedsUpdate = false;

        // Case 1: New status is PENDING (coming from ASSIGNED, IN_TRANSIT, or FAILED)
        if (newStatus == DeliveryStatus.PENDING && 
            (currentStatus == DeliveryStatus.ASSIGNED || currentStatus == DeliveryStatus.IN_TRANSIT || currentStatus == DeliveryStatus.FAILED)) {
            
            if (livreur != null) {
                log.info("DeliveryRequest {} status changing to PENDING from {}. Detaching Livreur {}.", deliveryRequestId, currentStatus, livreur.getId());
                deliveryRequest.setLivreur(null); // Remove livreur association

                // Check if livreur has other active deliveries
                if (hasNoOtherActiveDeliveries(livreur, deliveryRequestId)) {
                    log.info("Livreur {} has no other active deliveries. Setting available to true.", livreur.getId());
                    livreur.setDisponible(true);
                    livreurNeedsUpdate = true;
                }
            } else {
                 log.warn("DeliveryRequest {} status changing to PENDING from {}, but no livreur was assigned.", deliveryRequestId, currentStatus);
            }
        }
        // Case 2: New status is SUCCESSFUL or FAILED (coming from ASSIGNED or IN_TRANSIT)
        else if ((newStatus == DeliveryStatus.DELIVERED || newStatus == DeliveryStatus.FAILED) && 
                 (currentStatus == DeliveryStatus.ASSIGNED || currentStatus == DeliveryStatus.IN_TRANSIT)) {

            if (livreur != null) {
                 log.info("DeliveryRequest {} status changing to {} from {}. Checking Livreur {} availability.", deliveryRequestId, newStatus, currentStatus, livreur.getId());
                 // Keep livreur association, but check if they can become available
                 if (hasNoOtherActiveDeliveries(livreur, deliveryRequestId)) {
                     log.info("Livreur {} has no other active deliveries (excluding this one). Setting available to true.", livreur.getId());
                     livreur.setDisponible(true);
                     livreurNeedsUpdate = true;
                 }
            } else {
                 // This case might be less common, but log it
                 log.warn("DeliveryRequest {} status changing to {} from {}, but no livreur was assigned.", deliveryRequestId, newStatus, currentStatus);
            }
        }
        // Case 3: ASSIGNED -> IN_TRANSIT (No livreur changes needed)
        else if (newStatus == DeliveryStatus.IN_TRANSIT && currentStatus == DeliveryStatus.ASSIGNED) {
             log.info("DeliveryRequest {} status changing from ASSIGNED to IN_TRANSIT.", deliveryRequestId);
             // No specific livreur availability logic needed here
        }
        // --- Add checks for other potentially invalid transitions if needed --- 
        // e.g., PENDING -> IN_TRANSIT is invalid without assignment
        // e.g., FAILED -> ASSIGNED/IN_TRANSIT requires assigning a livreur, not handled here.

        // Update the status on the delivery request
        deliveryRequest.setStatus(newStatus);
        DeliveryRequest savedRequest = deliveryRequestRepository.save(deliveryRequest);

        // Save livreur if their availability changed
        if (livreurNeedsUpdate && livreur != null) {
            livreurRepository.save(livreur);
             log.info("Saved updated availability for Livreur {}.", livreur.getId());
        }

        log.info("Successfully updated status for DeliveryRequest {} from {} to {}", deliveryRequestId, currentStatus, newStatus);
        return mapToDTO(savedRequest);
    }

    /**
     * Checks if a livreur has any other delivery requests currently in ASSIGNED or IN_TRANSIT status,
     * excluding the specified delivery request ID.
     */
    private boolean hasNoOtherActiveDeliveries(Livreur livreur, Long currentDeliveryRequestId) {
        long activeDeliveriesCount = deliveryRequestRepository.countByLivreurAndStatusInAndIdNot(
                livreur, 
                Arrays.asList(DeliveryStatus.ASSIGNED, DeliveryStatus.IN_TRANSIT),
                currentDeliveryRequestId
        );
        return activeDeliveriesCount == 0;
    }

    private DeliveryRequestDTO mapToDTO(DeliveryRequest deliveryRequest) {
        Long livreurId = (deliveryRequest.getLivreur() != null) ? deliveryRequest.getLivreur().getId() : null;
        return new DeliveryRequestDTO(
            deliveryRequest.getId(),
            deliveryRequest.getDeliveryFee(),
            deliveryRequest.getStatus(),
            deliveryRequest.getOrder().getId(),
            livreurId,
            deliveryRequest.getDestinationLat(),
            deliveryRequest.getDestinationLon()
        );
    }

    private double calculateFee(double destinationLat, double destinationLon, double totalWeightKg, ShippingProperties props) {
        double originLat = props.getOriginLat();
        double originLon = props.getOriginLon();

        // Use the existing haversine method
        double distance = haversine(originLat, originLon, destinationLat, destinationLon);

        // Calculate fee using injected properties
        double distanceFee = distance * props.getPricePerKm();
        double weightFee = totalWeightKg * props.getPricePerKg();

        return distanceFee + weightFee + props.getFixedCost();
    }

    private static final double EARTH_RADIUS = 6371;

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c;
    }

    @Override
    @Transactional
    public void deleteDeliveryRequest(Long deliveryRequestId) {
        log.info("Attempting to delete delivery request with ID: {}", deliveryRequestId);
        // 1. Fetch the DeliveryRequest
        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(deliveryRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with ID: " + deliveryRequestId));

        // 2. Check the status
        DeliveryStatus currentStatus = deliveryRequest.getStatus();
        if (!(currentStatus != DeliveryStatus.IN_TRANSIT && currentStatus != DeliveryStatus.DELIVERED && currentStatus != DeliveryStatus.FAILED)) {
            String errorMessage = String.format(
                "Cannot delete DeliveryRequest %d because its status is %s. Deletion is not allowed for IN_TRANSIT, DELIVERED or FAILED statuses.",
                deliveryRequestId,
                currentStatus
            );
             log.warn(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        // 3. Handle Livreur availability
        Livreur livreur = deliveryRequest.getLivreur();
        if (livreur != null) {
            log.info("Delivery request {} has assigned Livreur {}. Checking for other active deliveries.", deliveryRequestId, livreur.getId());
            if (hasNoOtherActiveDeliveries(livreur, deliveryRequestId)) {
                log.info("Livreur {} has no other active deliveries. Setting disponible to true.", livreur.getId());
                livreur.setDisponible(true);
                livreurRepository.save(livreur); // Save the updated livreur status
            } else {
                 log.info("Livreur {} still has other active deliveries. Availability remains false.", livreur.getId());
            }
        } else {
            // This case *shouldn't* happen if status is ASSIGNED/IN_TRANSIT, but log it defensively.
             log.warn("Delivery request {} has status {} but no assigned livreur. Proceeding with deletion.", deliveryRequestId, currentStatus);
        }

        // 4. Delete the DeliveryRequest
        deliveryRequestRepository.delete(deliveryRequest);
        log.info("Called repository.delete() for DeliveryRequest ID: {}", deliveryRequestId);

        // Re-check if livreur was updated and log final state before method return
        if (livreur != null && livreur.isDisponible()) {
           log.info("Livreur {} availability was updated to true within the transaction.", livreur.getId());
        }

        log.info("Successfully completed deleteDeliveryRequest method for ID: {}", deliveryRequestId);
    }

    @Override
    public List<DeliveryRequestDTO> getAllDeliveryRequests(DeliveryStatus status, Long livreurId, Long orderId) {
        log.info("Fetching delivery requests with filters - Status: {}, LivreurID: {}, OrderID: {}", 
                 status, livreurId, orderId);

        // Build dynamic query using Specifications
        Specification<DeliveryRequest> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (livreurId != null) {
                // Join with Livreur entity to filter by its ID
                predicates.add(criteriaBuilder.equal(root.get("livreur").get("id"), livreurId));
            }
            if (orderId != null) {
                // Join with Order entity to filter by its ID
                predicates.add(criteriaBuilder.equal(root.get("order").get("id"), orderId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<DeliveryRequest> deliveryRequests = deliveryRequestRepository.findAll(spec);

        log.info("Found {} delivery requests matching criteria.", deliveryRequests.size());

        // Map entities to DTOs
        return deliveryRequests.stream()
                .map(this::mapToDTO) // Reuse existing mapping method
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeliveryRequestDTO assignLivreurManually(Long deliveryRequestId, Long livreurId) {
        log.info("Attempting to manually assign Livreur {} to DeliveryRequest {}", livreurId, deliveryRequestId);

        // 1. Fetch DeliveryRequest
        DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(deliveryRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest not found with ID: " + deliveryRequestId));

        // 2. Fetch Livreur
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur not found with ID: " + livreurId));

        // 3. Check if DeliveryRequest already assigned
        if (deliveryRequest.getLivreur() != null) {
            throw new DeliveryAlreadyAssignedException(
                String.format("DeliveryRequest %d is already assigned to Livreur %d.", 
                              deliveryRequestId, deliveryRequest.getLivreur().getId())
            );
        }
        
        // Can only assign to PENDING requests typically
        if (deliveryRequest.getStatus() != DeliveryStatus.PENDING) {
            throw new IllegalStateException(
                String.format("DeliveryRequest %d has status %s and cannot be manually assigned. Only PENDING requests are eligible.", 
                              deliveryRequestId, deliveryRequest.getStatus())
            );
        }


        // 4. Check if Livreur is available
        if (!livreur.isDisponible()) {
            throw new IllegalStateException("Livreur with ID " + livreurId + " is not available for assignment.");
        }

        // 5. Perform assignment
        deliveryRequest.setLivreur(livreur);
        deliveryRequest.setStatus(DeliveryStatus.ASSIGNED);
        
        // 6. Mark Livreur as unavailable
        livreur.setDisponible(false);
        
        // 7. Save changes
        livreurRepository.save(livreur);
        DeliveryRequest savedRequest = deliveryRequestRepository.save(deliveryRequest);

        log.info("Successfully assigned Livreur {} to DeliveryRequest {} and updated status to ASSIGNED.", livreurId, deliveryRequestId);

        // 8. Return DTO
        return mapToDTO(savedRequest);
    }
}
