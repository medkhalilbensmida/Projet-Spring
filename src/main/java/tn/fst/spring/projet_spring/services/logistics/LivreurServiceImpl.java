package tn.fst.spring.projet_spring.services.logistics;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurRequest;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class LivreurServiceImpl implements ILivreurService {

    LivreurRepository livreurRepository;

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
} 