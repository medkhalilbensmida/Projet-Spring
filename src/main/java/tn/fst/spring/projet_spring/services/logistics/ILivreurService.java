package tn.fst.spring.projet_spring.services.logistics;

import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurRequest;
import tn.fst.spring.projet_spring.model.logistics.Livreur;

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
} 