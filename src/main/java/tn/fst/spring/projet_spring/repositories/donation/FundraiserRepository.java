package tn.fst.spring.projet_spring.repositories.donation;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.donation.Fundraiser;

public interface FundraiserRepository extends JpaRepository<Fundraiser, Long> {
}
