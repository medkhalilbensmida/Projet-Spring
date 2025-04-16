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
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Payment> findBySuccessful(boolean successful);

    // New methods to filter by user ID
    List<Payment> findByOrderUserId(Long userId);
    List<Payment> findByPaymentMethodAndOrderUserId(PaymentMethod paymentMethod, Long userId);
    List<Payment> findByPaymentDateBetweenAndOrderUserId(LocalDateTime start, LocalDateTime end, Long userId);
    List<Payment> findBySuccessfulAndOrderUserId(boolean successful, Long userId);
}