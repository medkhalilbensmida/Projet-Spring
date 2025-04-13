package tn.fst.spring.projet_spring.repositories.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.payment.Payment;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Payment> findBySuccessful(boolean successful);
}