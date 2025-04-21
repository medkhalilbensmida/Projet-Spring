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

    // Récupération des factures - méthodes simplifiées
    Object getInvoiceById(Long id, boolean details);

    InvoiceDetailResponse getInvoiceDetailsById(Long id);

    Object getInvoiceByNumber(String invoiceNumber, boolean details);

    InvoiceDetailResponse getInvoiceDetailsByNumber(String invoiceNumber);

    List<InvoiceResponse> getInvoicesByUser(Long userId);

    List<InvoiceResponse> getInvoicesByType(InvoiceType type);

    List<InvoiceResponse> getInvoicesByTypeAndUser(InvoiceType type, Long userId);

    List<InvoiceResponse> getInvoicesByDateRange(LocalDateTime start, LocalDateTime end);

    List<InvoiceResponse> getAllInvoices();

    // Méthode de recherche flexible consolidée
    List<InvoiceResponse> searchInvoices(
            Long userId,
            InvoiceType type,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean isPaid,
            String invoiceNumber,
            String orderNumber
    );

    // Génération de PDF
    byte[] generateInvoicePdf(Long id);

    // Mise à jour et suppression
    InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest);
    void deleteInvoice(Long id);
}