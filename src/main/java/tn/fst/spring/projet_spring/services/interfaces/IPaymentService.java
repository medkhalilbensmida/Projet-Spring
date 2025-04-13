package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.payment.PaymentRequest;
import tn.fst.spring.projet_spring.dto.payment.PaymentResponse;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

public interface IPaymentService {
    PaymentResponse processPayment(PaymentRequest paymentRequest);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse getPaymentByTransactionId(String transactionId);
    PaymentResponse getPaymentByOrderId(Long orderId);
    List<PaymentResponse> getAllPayments();
    List<PaymentResponse> getPaymentsByPaymentMethod(PaymentMethod paymentMethod);
    List<PaymentResponse> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    List<PaymentResponse> getPaymentsByStatus(boolean successful); // Consolidated method
    void deletePayment(Long id);
}