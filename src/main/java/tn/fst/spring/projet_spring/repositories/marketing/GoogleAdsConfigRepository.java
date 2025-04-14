package tn.fst.spring.projet_spring.repositories.marketing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.entities.marketing.config.GoogleAdsConfig;

@Repository
public interface GoogleAdsConfigRepository extends JpaRepository<GoogleAdsConfig, Long> {
    // Méthodes personnalisées si nécessaire
}