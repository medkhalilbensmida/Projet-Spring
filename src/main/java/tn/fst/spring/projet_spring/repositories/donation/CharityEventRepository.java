package tn.fst.spring.projet_spring.repositories.donation;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.donation.CharityEvent;

public interface CharityEventRepository extends JpaRepository<CharityEvent, Long> {
}
