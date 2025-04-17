package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.invoice.InvoiceDetailResponse;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceRequest;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceResponse;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceSearchRequest;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;

import java.time.LocalDateTime;
import java.util.List;

public interface IInvoiceService {
    // Génération automatique pour les ventes en ligne
    InvoiceResponse generateInvoiceForOrder(Long orderId);

    // Création manuelle pour les livraisons
    InvoiceResponse createInvoice(InvoiceRequest invoiceRequest);

    // Récupération des factures
    InvoiceResponse getInvoiceById(Long id);
    InvoiceDetailResponse getInvoiceDetailsById(Long id);
    InvoiceResponse getInvoiceByNumber(String invoiceNumber);
    InvoiceDetailResponse getInvoiceDetailsByNumber(String invoiceNumber);
    List<InvoiceResponse> getInvoicesByUser(Long userId);
    List<InvoiceResponse> getInvoicesByType(InvoiceType type);
    List<InvoiceResponse> getInvoicesByTypeAndUser(InvoiceType type, Long userId);
    List<InvoiceResponse> getInvoicesByDateRange(LocalDateTime start, LocalDateTime end);
    List<InvoiceResponse> getAllInvoices();
    List<InvoiceResponse> searchInvoices(InvoiceSearchRequest searchRequest);

    // Génération de PDF
    byte[] generateInvoicePdf(Long id);

    // Mise à jour et suppression
    InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest);
    void deleteInvoice(Long id);
}