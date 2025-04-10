package tn.fst.spring.projet_spring.repositories.marketing;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.entities.marketing.TargetedAudience;

public interface TargetedAudienceRepository extends JpaRepository<TargetedAudience, Long> {
}
