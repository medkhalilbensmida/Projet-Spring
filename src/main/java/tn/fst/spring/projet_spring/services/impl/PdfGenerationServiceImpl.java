package tn.fst.spring.projet_spring.services.impl;

import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceDetailResponse;
import tn.fst.spring.projet_spring.services.interfaces.IPdfGenerationService;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfGenerationServiceImpl implements IPdfGenerationService {
    private final TemplateEngine templateEngine;

    @Override
    public byte[] generateInvoicePdf(InvoiceDetailResponse invoice) {
        try {
            // Préparer le contexte pour le template
            Context context = new Context(Locale.FRANCE);
            context.setVariable("invoice", invoice);

            // Format des dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            context.setVariable("issueDateFormatted", invoice.getIssueDate().format(dateFormatter));
            if (invoice.getDueDate() != null) {
                context.setVariable("dueDateFormatted", invoice.getDueDate().format(dateFormatter));
            }

            // Traitement du template HTML
            String htmlContent = templateEngine.process("invoice/invoice-template", context);

            // Conversion HTML en PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}