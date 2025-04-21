package tn.fst.spring.projet_spring.repositories.donation;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.donation.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
