package tn.fst.spring.projet_spring.dto.invoice;

import lombok.Builder;
import lombok.Data;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;

import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private LocalDateTime issueDate;
    private double amount;
    private boolean isPaid;
    private InvoiceType type;
    private String billingAddress;
    private String shippingAddress;
    private LocalDateTime dueDate;
    private String taxId;
    private double taxAmount;
}