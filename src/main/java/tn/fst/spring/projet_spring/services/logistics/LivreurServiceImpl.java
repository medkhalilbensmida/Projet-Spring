package tn.fst.spring.projet_spring.services.logistics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurRequest;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;
import tn.fst.spring.projet_spring.repositories.logistics.DeliveryRequestRepository;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

@Service
@Slf4j
public class LivreurServiceImpl implements ILivreurService {

    private final LivreurRepository livreurRepository;
    private final DeliveryRequestRepository deliveryRequestRepository;

    private static final double BONUS_FACTOR_PER_DELIVERY = 10.0;

    public LivreurServiceImpl(LivreurRepository livreurRepository, DeliveryRequestRepository deliveryRequestRepository) {
        this.livreurRepository = livreurRepository;
        this.deliveryRequestRepository = deliveryRequestRepository;
    }

    @Override
    public List<Livreur> retrieveAllLivreurs() {
        return livreurRepository.findAll();
    }

    @Override
    public Livreur addLivreur(Livreur l) {
        return livreurRepository.save(l);
    }

    @Override
    public Livreur updateLivreur(Long id, UpdateLivreurRequest livreurRequest) {
        Optional<Livreur> optionalLivreur = livreurRepository.findById(id);
        if (optionalLivreur.isEmpty()) {
            return null;
        }

        Livreur existingLivreur = optionalLivreur.get();

        if (livreurRequest.getNom() != null) {
            existingLivreur.setNom(livreurRequest.getNom());
        }
        if (livreurRequest.getDisponible() != null) {
            existingLivreur.setDisponible(livreurRequest.getDisponible());
        }

        return livreurRepository.save(existingLivreur);
    }

    @Override
    public Livreur retrieveLivreur(Long id) {
        return livreurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livreur not found with id: " + id)); // Consider a custom exception
    }

    @Override
    public void removeLivreur(Long id) {
        livreurRepository.deleteById(id);
    }

    @Override
    public Livreur updateLivreurAvailability(Long id, boolean disponible) {
        Optional<Livreur> optionalLivreur = livreurRepository.findById(id);
        if (optionalLivreur.isEmpty()) {
            return null; // Or throw exception
        }
        Livreur existingLivreur = optionalLivreur.get();
        existingLivreur.setDisponible(disponible);
        return livreurRepository.save(existingLivreur);
    }

    @Override
    public double calculatePrime(Long livreurId) {
        if (!livreurRepository.existsById(livreurId)) {
            throw new RuntimeException("Livreur not found with id: " + livreurId);
        }

        long deliveredCount = deliveryRequestRepository.countByLivreurIdAndStatus(livreurId, DeliveryStatus.DELIVERED);

        double prime = deliveredCount * BONUS_FACTOR_PER_DELIVERY;

        log.info("Calculated prime for livreur {}: {} deliveries * {} = {}", livreurId, deliveredCount, BONUS_FACTOR_PER_DELIVERY, prime);

        return prime;
    }

    @Override
    public List<DeliveryRequest> getAssignedDeliveries(Long livreurId) {
        return deliveryRequestRepository.findByLivreurId(livreurId);
    }

    @Override
    public Livreur getLivreurOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMonths(1);
        List<DeliveryRequest> delivered = deliveryRequestRepository.findByStatusAndOrder_OrderDateBetween(DeliveryStatus.DELIVERED, start, end);
        if (delivered.isEmpty()) {
            return null;
        }
        Map<Livreur, Long> counts = delivered.stream()
            .map(DeliveryRequest::getLivreur)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return counts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .get()
            .getKey();
    }
} 