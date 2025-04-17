package tn.fst.spring.projet_spring.services.logistics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurRequest;
import tn.fst.spring.projet_spring.dto.logistics.LivreurStatsDTO;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;
import tn.fst.spring.projet_spring.repositories.logistics.DeliveryRequestRepository;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import tn.fst.spring.projet_spring.exception.ResourceNotFoundException;

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
            throw new ResourceNotFoundException("Livreur not found with id: " + id);
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
                .orElseThrow(() -> new ResourceNotFoundException("Livreur not found with id: " + id));
    }

    @Override
    public void removeLivreur(Long id) {
        livreurRepository.deleteById(id);
    }

    @Override
    public Livreur updateLivreurAvailability(Long id, boolean disponible) {
        Optional<Livreur> optionalLivreur = livreurRepository.findById(id);
        if (optionalLivreur.isEmpty()) {
            throw new ResourceNotFoundException("Livreur not found with id: " + id);
        }
        Livreur existingLivreur = optionalLivreur.get();
        existingLivreur.setDisponible(disponible);
        return livreurRepository.save(existingLivreur);
    }

    @Override
    public double calculatePrime(Long livreurId) {
        if (!livreurRepository.existsById(livreurId)) {
            throw new ResourceNotFoundException("Livreur not found with id: " + livreurId);
        }

        long deliveredCount = deliveryRequestRepository.countByLivreurIdAndStatus(livreurId, DeliveryStatus.DELIVERED);

        double prime = deliveredCount * BONUS_FACTOR_PER_DELIVERY;

        log.info("Calculated prime for livreur {}: {} deliveries * {} = {}", livreurId, deliveredCount, BONUS_FACTOR_PER_DELIVERY, prime);

        return prime;
    }

    @Override
    public List<DeliveryRequest> getAssignedDeliveries(Long livreurId) {
        if (!livreurRepository.existsById(livreurId)) {
            throw new ResourceNotFoundException("Livreur not found with id: " + livreurId);
        }
        return deliveryRequestRepository.findByLivreurId(livreurId);
    }

    @Override
    public Livreur getLivreurOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMonths(1);
        List<DeliveryRequest> delivered = deliveryRequestRepository.findByStatusAndOrderOrderDateBetween(DeliveryStatus.DELIVERED, start, end);
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

    @Override
    public List<LivreurStatsDTO> getLivreurStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMonths(1);
        List<DeliveryRequest> deliveredThisMonthList = deliveryRequestRepository.findByStatusAndOrderOrderDateBetween(DeliveryStatus.DELIVERED, start, end);
        Map<Long, Long> monthCount = deliveredThisMonthList.stream()
            .filter(dr -> dr.getLivreur() != null)
            .collect(Collectors.groupingBy(dr -> dr.getLivreur().getId(), Collectors.counting()));
        return livreurRepository.findAll().stream().map(l -> {
            Long id = l.getId();
            long a = deliveryRequestRepository.countByLivreurIdAndStatus(id, DeliveryStatus.ASSIGNED);
            long i = deliveryRequestRepository.countByLivreurIdAndStatus(id, DeliveryStatus.IN_TRANSIT);
            long d = deliveryRequestRepository.countByLivreurIdAndStatus(id, DeliveryStatus.DELIVERED);
            long f = deliveryRequestRepository.countByLivreurIdAndStatus(id, DeliveryStatus.FAILED);
            long total = a + i + d + f;
            double pa = total == 0 ? 0 : a * 100.0 / total;
            double pi = total == 0 ? 0 : i * 100.0 / total;
            double pd = total == 0 ? 0 : d * 100.0 / total;
            double pf = total == 0 ? 0 : f * 100.0 / total;
            long dm = monthCount.getOrDefault(id, 0L);
            return new LivreurStatsDTO(id, l.getNom(), pa, pi, pd, pf, dm);
        }).collect(Collectors.toList());
    }
} 