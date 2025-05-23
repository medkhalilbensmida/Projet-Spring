package tn.fst.spring.projet_spring.repositories.logistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.logistics.Livreur;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long>, JpaSpecificationExecutor<DeliveryRequest> {

    // Method to find delivery requests by livreur ID and status
    @Query("SELECT COUNT(dr) FROM DeliveryRequest dr WHERE dr.livreur.id = :livreurId AND dr.status = :status")
    long countByLivreurIdAndStatus(@Param("livreurId") Long livreurId, @Param("status") DeliveryStatus status);

    // Optional: If you need the full list of delivered requests for a person
    List<DeliveryRequest> findByLivreurIdAndStatus(Long livreurId, DeliveryStatus status);

    Optional<DeliveryRequest> findByOrder(Order order);

    // Find all deliveries assigned to a livreur
    List<DeliveryRequest> findByLivreurId(Long livreurId);

    // Find delivered deliveries within a date range (used for livreur of the month)
    List<DeliveryRequest> findByStatusAndOrderOrderDateBetween(DeliveryStatus status, LocalDateTime start, LocalDateTime end);

    // Find deliveries by status
    List<DeliveryRequest> findByStatus(DeliveryStatus status);

    // Find deliveries assigned to a specific livreur
    List<DeliveryRequest> findByLivreur(Livreur livreur);

    // Count active deliveries for a livreur excluding a specific delivery ID
    long countByLivreurAndStatusInAndIdNot(Livreur livreur, List<DeliveryStatus> statuses, Long excludeDeliveryId);
}