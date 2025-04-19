package tn.fst.spring.projet_spring.services.logistics;

import tn.fst.spring.projet_spring.dto.logistics.CreateDeliveryRequestDTO;
import tn.fst.spring.projet_spring.dto.logistics.DeliveryRequestDTO;

public interface IDeliveryRequestService {
    /**
     * Crée une demande de livraison en calculant les frais dynamiquement
     */
    DeliveryRequestDTO create(CreateDeliveryRequestDTO dto);

    /**
     * Calculates the delivery fee based on destination coordinates and total weight,
     * using configured shipping properties.
     *
     * @param destinationLat Latitude of the destination.
     * @param destinationLon Longitude of the destination.
     * @param totalWeightKg Total weight of the items in kilograms.
     * @return The calculated delivery fee.
     * @throws IllegalArgumentException if weight is negative or coordinates are invalid.
     */
    double calculateDeliveryFee(double destinationLat, double destinationLon, double totalWeightKg);

    // New method to calculate fee based on an existing DeliveryRequest ID
    double calculateFeeForRequest(Long deliveryRequestId);
    
    /**
     * Auto-assigns the closest available livreur to a delivery request.
     * The assignment is based on the distance between the livreur and the delivery destination.
     * 
     * @param deliveryRequestId The ID of the delivery request to assign
     * @return The updated DeliveryRequestDTO with the assigned livreur information
     * @throws ResourceNotFoundException if the delivery request or no available livreurs are found
     */
    DeliveryRequestDTO autoAssignLivreur(Long deliveryRequestId);
}
