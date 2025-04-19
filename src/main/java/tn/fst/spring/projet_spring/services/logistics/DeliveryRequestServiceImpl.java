package tn.fst.spring.projet_spring.services.logistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.fst.spring.projet_spring.config.ShippingProperties;
import tn.fst.spring.projet_spring.dto.logistics.CreateDeliveryRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.DeliveryRequestDTO;
import tn.fst.spring.projet_spring.exception.DeliveryAlreadyAssignedException;
import tn.fst.spring.projet_spring.exception.ResourceNotFoundException;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.repositories.logistics.DeliveryRequestRepository;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
}
