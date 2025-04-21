package tn.fst.spring.projet_spring.controllers.invoice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "Récupérer une facture par son ID avec option détails")
    public ResponseEntity<Object> getInvoiceById(
            @PathVariable Long id,
            @Parameter(description = "Inclure les détails complets de la facture")
            @RequestParam(required = false, defaultValue = "false") boolean details) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id, details));
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Récupérer une facture par son numéro avec option détails")
    public ResponseEntity<Object> getInvoiceByNumber(
            @PathVariable String invoiceNumber,
            @Parameter(description = "Inclure les détails complets de la facture")
            @RequestParam(required = false, defaultValue = "false") boolean details) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber, details));
    }

    @GetMapping
    @Operation(summary = "Rechercher des factures avec filtres optionnels")
    public ResponseEntity<List<InvoiceResponse>> searchInvoices(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) InvoiceType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false) String orderNumber) {

        return ResponseEntity.ok(invoiceService.searchInvoices(
                userId, type, startDate, endDate, isPaid, invoiceNumber, orderNumber));
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