package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.invoice.InvoiceDetailResponse;

public interface IPdfGenerationService {
    byte[] generateInvoicePdf(InvoiceDetailResponse invoice);
}