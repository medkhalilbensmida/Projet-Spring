package tn.fst.spring.projet_spring.repositories.donation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.donation.Donation;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Query("SELECT d FROM Donation d JOIN FETCH d.product JOIN FETCH d.donor JOIN FETCH d.event")
    List<Donation> findAllWithAssociations();
}
