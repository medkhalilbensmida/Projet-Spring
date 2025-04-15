package tn.fst.spring.projet_spring.services.logistics;

import tn.fst.spring.projet_spring.dto.logistics.UpdateLivreurRequest;
import tn.fst.spring.projet_spring.model.logistics.Livreur;

import java.util.List;

public interface ILivreurService {
    List<Livreur> retrieveAllLivreurs();
    Livreur addLivreur(Livreur l);
    Livreur updateLivreur(Long id, UpdateLivreurRequest l);
    Livreur retrieveLivreur(Long id);
    void removeLivreur(Long id);
} 