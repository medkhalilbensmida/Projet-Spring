package tn.fst.spring.projet_spring.services.logistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.fst.spring.projet_spring.config.ShippingProperties;
import tn.fst.spring.projet_spring.dto.logistics.CreateDeliveryRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.DeliveryRequestDTO;
import tn.fst.spring.projet_spring.exception.ResourceNotFoundException;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.repositories.logistics.DeliveryRequestRepository;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;

@Service
@RequiredArgsConstructor
public class DeliveryRequestServiceImpl implements IDeliveryRequestService {

    private final DeliveryRequestRepository deliveryRequestRepository;
    private final OrderRepository orderRepository;
    private final LivreurRepository livreurRepository;
    private final ShippingProperties shippingProperties;

    @Override
    @Transactional
    public DeliveryRequestDTO create(CreateDeliveryRequestDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        if (deliveryRequestRepository.findByOrder(order).isPresent()) {
            throw new IllegalStateException("Delivery request already exists for order " + order.getOrderNumber());
        }

        Livreur livreur = livreurRepository.findById(dto.getLivreurId())
                .orElseThrow(() -> new ResourceNotFoundException("Livreur not found with id: " + dto.getLivreurId()));

        double totalWeight = order.getItems().stream()
                .mapToDouble(item -> (item.getProduct() != null ? item.getProduct().getWeight() : 0.0) * item.getQuantity())
                .sum();

        double fee = calculateDeliveryFee(dto.getDestinationLat(), dto.getDestinationLon(), totalWeight);

        DeliveryRequest dr = new DeliveryRequest();
        dr.setOrder(order);
        dr.setLivreur(livreur);
        dr.setStatus(DeliveryStatus.ASSIGNED);
        dr.setDeliveryFee(fee);
        dr.setDestinationLat(dto.getDestinationLat());
        dr.setDestinationLon(dto.getDestinationLon());

        DeliveryRequest saved = deliveryRequestRepository.save(dr);

        return new DeliveryRequestDTO(
                saved.getId(),
                saved.getDeliveryFee(),
                saved.getStatus(),
                saved.getOrder().getId(),
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
