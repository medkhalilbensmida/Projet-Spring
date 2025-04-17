package tn.fst.spring.projet_spring.dto.invoice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;

@Data
public class InvoiceRequest {
    @NotNull
    private Long orderId;

    private InvoiceType type = InvoiceType.ONLINE; // Par défaut pour les factures automatiques

    private String billingAddress;

    private String shippingAddress;

    private String notes;

    private String taxId;

    @Positive
    private Double taxAmount;
}