package tn.fst.spring.projet_spring.dto.payment;

import lombok.Builder;
import lombok.Data;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private String transactionId;
    private PaymentMethod paymentMethod;
    private double amount;
    private LocalDateTime paymentDate;
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String cardLastFourDigits;
    private String paymentProviderReference;
    private boolean successful;
    private String notes;
}