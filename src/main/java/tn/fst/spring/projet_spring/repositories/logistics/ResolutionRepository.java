package tn.fst.spring.projet_spring.repositories.logistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.logistics.Resolution;
import tn.fst.spring.projet_spring.model.logistics.ResolutionStatus;
import tn.fst.spring.projet_spring.model.logistics.ResolutionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, Long> {
    Optional<Resolution> findByComplaintId(Long complaintId);
    List<Resolution> findByType(ResolutionType type);
    List<Resolution> findByStatus(ResolutionStatus status);
}
