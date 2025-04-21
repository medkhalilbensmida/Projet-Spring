package tn.fst.spring.projet_spring.repositories.logistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.logistics.Complaint;
import tn.fst.spring.projet_spring.model.logistics.ComplaintStatus;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserId(Long userId);
    List<Complaint> findByOrderId(Long orderId);
    List<Complaint> findByStatus(ComplaintStatus status);
}