package tn.fst.spring.projet_spring.repositories.logistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.logistics.Livreur;

import java.util.List;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, Long> {
    /**
     * Find livreurs by their availability status.
     * 
     * @param disponible The availability status to filter by
     * @return List of livreurs with the specified availability
     */
    List<Livreur> findByDisponible(boolean disponible);
} 