package tn.fst.spring.projet_spring.controllers.invoice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceDetailResponse;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceRequest;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceResponse;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceSearchRequest;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;
import tn.fst.spring.projet_spring.services.interfaces.IInvoiceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Factures", description = "API de gestion des factures")
public class InvoiceController {
    private final IInvoiceService invoiceService;

    @PostMapping("/generate/{orderId}")
    @Operation(summary = "Générer une facture automatiquement pour une commande")
    public ResponseEntity<InvoiceResponse> generateInvoice(@PathVariable Long orderId) {
        return new ResponseEntity<>(invoiceService.generateInvoiceForOrder(orderId), HttpStatus.CREATED);
    }

    @PostMapping
    @Operation(summary = "Créer une facture manuellement")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        return new ResponseEntity<>(invoiceService.createInvoice(invoiceRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une facture par son ID")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Récupérer les détails d'une facture par son ID")
    public ResponseEntity<InvoiceDetailResponse> getInvoiceDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceDetailsById(id));
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Récupérer une facture par son numéro")
    public ResponseEntity<InvoiceResponse> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping("/number/{invoiceNumber}/details")
    @Operation(summary = "Récupérer les détails d'une facture par son numéro")
    public ResponseEntity<InvoiceDetailResponse> getInvoiceDetailsByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceDetailsByNumber(invoiceNumber));
    }

    @GetMapping
    @Operation(summary = "Récupérer les factures avec filtres optionnels")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) InvoiceType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (userId != null && type != null) {
            return ResponseEntity.ok(invoiceService.getInvoicesByTypeAndUser(type, userId));
        } else if (userId != null) {
            return ResponseEntity.ok(invoiceService.getInvoicesByUser(userId));
        } else if (type != null) {
            return ResponseEntity.ok(invoiceService.getInvoicesByType(type));
        } else if (startDate != null && endDate != null) {
            return ResponseEntity.ok(invoiceService.getInvoicesByDateRange(startDate, endDate));
        } else {
            return ResponseEntity.ok(invoiceService.getAllInvoices());
        }
    }

    @PostMapping("/search")
    @Operation(summary = "Recherche avancée de factures")
    public ResponseEntity<List<InvoiceResponse>> searchInvoices(@RequestBody InvoiceSearchRequest searchRequest) {
        return ResponseEntity.ok(invoiceService.searchInvoices(searchRequest));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Générer un PDF de la facture")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        byte[] pdfBytes = invoiceService.generateInvoicePdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une facture")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceRequest invoiceRequest) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une facture")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}