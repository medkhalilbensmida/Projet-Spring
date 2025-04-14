package tn.fst.spring.projet_spring.services.logistics;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;

import java.util.List;

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
    public Livreur updateLivreur(Livreur l) {
        // Ensure the livreur exists before updating
        retrieveLivreur(l.getId());
        return livreurRepository.save(l);
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
} 