package tn.fst.spring.projet_spring.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;

@Data
public class PaymentRequest {
    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    @Positive
    private Double amount;

    private String cardLastFourDigits;

    private String paymentProviderReference;

    private String notes;
}