package tn.fst.spring.projet_spring.dto.invoice;

import lombok.Data;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;

import java.time.LocalDateTime;

@Data
public class InvoiceSearchRequest {
    private Long userId;
    private InvoiceType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isPaid;
    private String invoiceNumber;
    private String orderNumber;
}