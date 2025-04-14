package tn.fst.spring.projet_spring.controllers.logistics;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.services.logistics.ILivreurService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/livreurs")
public class LivreurRestController {

    ILivreurService livreurService;

    @GetMapping
    public List<Livreur> getAllLivreurs() {
        return livreurService.retrieveAllLivreurs();
    }

    @GetMapping("/{id}")
    public Livreur getLivreurById(@PathVariable Long id) {
        return livreurService.retrieveLivreur(id);
    }

    @PostMapping
    public Livreur createLivreur(@RequestBody Livreur livreur) {
        return livreurService.addLivreur(livreur);
    }

    @PutMapping("/{id}")
    public Livreur updateLivreur(@PathVariable Long id, @RequestBody Livreur livreur) {
        // Ensure the ID in the path matches the ID in the body, if provided
        if (livreur.getId() == null) {
            livreur.setId(id);
        } else if (!livreur.getId().equals(id)) {
            throw new IllegalArgumentException("ID in path must match ID in request body");
        }
        return livreurService.updateLivreur(livreur);
    }

    @DeleteMapping("/{id}")
    public void deleteLivreur(@PathVariable Long id) {
        livreurService.removeLivreur(id);
    }
} 