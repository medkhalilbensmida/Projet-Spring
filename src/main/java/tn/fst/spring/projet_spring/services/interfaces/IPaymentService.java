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

    // --- New Method for Refund ---
    /**
     * Initiates a refund for a specific order.
     * @param orderId The ID of the order to be refunded.
     * @param amount The amount to refund (should ideally match original payment or be specified).
     * @param reason A description or reason for the refund.
     * @throws tn.fst.spring.projet_spring.exception.ResourceNotFoundException if the original payment for the order is not found.
     * @throws IllegalStateException if the payment cannot be refunded (e.g., already refunded).
     * @throws RuntimeException for errors during the refund process (e.g., payment gateway issues).
     */
    void initiateRefund(Long orderId, Double amount, String reason);
}