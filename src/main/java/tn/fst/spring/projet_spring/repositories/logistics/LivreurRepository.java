package tn.fst.spring.projet_spring.repositories.logistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.logistics.Livreur;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, Long> {
} 