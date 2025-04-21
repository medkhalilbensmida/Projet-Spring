package tn.fst.spring.projet_spring.services.logistics;

import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurRequest;
import tn.fst.spring.projet_spring.dto.logistics.LivreurStatsDTO;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;

import java.util.List;

public interface ILivreurService {
    List<Livreur> retrieveAllLivreurs();
    Livreur addLivreur(Livreur l);
    Livreur updateLivreur(Long id, UpdateLivreurRequest l);
    Livreur updateLivreurAvailability(Long id, boolean disponible);
    Livreur retrieveLivreur(Long id);
    void removeLivreur(Long id);

    /**
     * Calculates the bonus (prime) for a livreur based on completed deliveries.
     * @param livreurId The ID of the livreur.
     * @return The calculated prime amount.
     */
    double calculatePrime(Long livreurId);

    /**
     * Stats per livreur: percentage by status and total delivered this month
     */
    List<LivreurStatsDTO> getLivreurStats();

    // Retrieve assigned deliveries for a livreur
    List<DeliveryRequest> getAssignedDeliveries(Long livreurId);
    // Find the livreur of the month based on deliveries in current month
    Livreur getLivreurOfMonth();
    Livreur updateLivreurCoordinates(Long id, Double latitude, Double longitude);
} 