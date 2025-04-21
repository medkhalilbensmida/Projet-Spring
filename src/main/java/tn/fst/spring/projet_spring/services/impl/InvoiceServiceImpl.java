package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceDetailResponse;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceRequest;
import tn.fst.spring.projet_spring.dto.invoice.InvoiceResponse;
import tn.fst.spring.projet_spring.dto.order.OrderItemResponse;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.invoice.Invoice;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.order.OrderItem;
import tn.fst.spring.projet_spring.model.payment.Payment;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.invoice.InvoiceRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;
import tn.fst.spring.projet_spring.security.SecurityUtil;
import tn.fst.spring.projet_spring.services.interfaces.IInvoiceService;
import tn.fst.spring.projet_spring.services.interfaces.IPdfGenerationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements IInvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final IPdfGenerationService pdfGenerationService;

    @Override
    @Transactional
    public InvoiceResponse generateInvoiceForOrder(Long orderId) {
        // Vérifier si une facture existe déjà pour cette commande
        if (invoiceRepository.existsByOrderId(orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une facture existe déjà pour cette commande");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande non trouvée"));

        // Vérification des permissions : seul un admin ou le propriétaire peut générer une facture
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!order.getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Vous n'êtes pas autorisé à générer une facture pour cette commande");
            }
        }

        // Vérification du paiement
        if (order.getPayment() == null || !order.getPayment().isSuccessful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La commande doit être payée avant de générer une facture");
        }

        // Création de la facture
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setAmount(order.getTotalAmount());
        invoice.setPaid(true); // Déjà payée pour les commandes en ligne
        invoice.setType(InvoiceType.ONLINE);

        // Adresse de facturation = adresse du client si disponible
        if (order.getCustomerAddress() != null) {
            invoice.setBillingAddress(order.getCustomerAddress());
            invoice.setShippingAddress(order.getCustomerAddress());
        }

        // Date d'échéance = date d'émission pour les factures déjà payées
        invoice.setDueDate(invoice.getIssueDate());

        // Calcul de la TVA (exemple: 20%)
        double taxRate = 0.20;
        double taxAmount = Math.round(order.getTotalAmount() * taxRate * 100) / 100.0;
        invoice.setTaxAmount(taxAmount);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Mettre à jour la relation avec la commande
        order.setInvoice(savedInvoice);
        orderRepository.save(order);

        return convertToResponse(savedInvoice);
    }

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        // Vérifier si une facture existe déjà pour cette commande
        if (invoiceRepository.existsByOrderId(invoiceRequest.getOrderId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une facture existe déjà pour cette commande");
        }

        Order order = orderRepository.findById(invoiceRequest.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande non trouvée"));

        // Seuls les admins peuvent créer des factures manuellement
        if (!securityUtil.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seuls les administrateurs peuvent créer des factures manuellement");
        }

        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setAmount(order.getTotalAmount());
        invoice.setPaid(order.getPayment() != null && order.getPayment().isSuccessful());
        invoice.setType(invoiceRequest.getType());
        invoice.setBillingAddress(invoiceRequest.getBillingAddress());
        invoice.setShippingAddress(invoiceRequest.getShippingAddress());
        invoice.setNotes(invoiceRequest.getNotes());

        // Date d'échéance = date d'émission + 30 jours pour les factures non payées
        if (!invoice.isPaid()) {
            invoice.setDueDate(invoice.getIssueDate().plusDays(30));
        } else {
            invoice.setDueDate(invoice.getIssueDate());
        }

        // Informations fiscales
        invoice.setTaxId(invoiceRequest.getTaxId());
        if (invoiceRequest.getTaxAmount() != null) {
            invoice.setTaxAmount(invoiceRequest.getTaxAmount());
        } else {
            // Calcul par défaut de la TVA (20%)
            double taxRate = 0.20;
            double taxAmount = Math.round(order.getTotalAmount() * taxRate * 100) / 100.0;
            invoice.setTaxAmount(taxAmount);
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Mettre à jour la relation avec la commande
        order.setInvoice(savedInvoice);
        orderRepository.save(order);

        return convertToResponse(savedInvoice);
    }

    @Override
    public Object getInvoiceById(Long id, boolean details) {
        Invoice invoice = findAndCheckAccess(id);
        return details ? convertToDetailResponse(invoice) : convertToResponse(invoice);
    }
   @Override
   public InvoiceDetailResponse getInvoiceDetailsById(Long id) {
        Invoice invoice = findAndCheckAccess(id);
        return convertToDetailResponse(invoice);
    }

    @Override
    public Object getInvoiceByNumber(String invoiceNumber, boolean details) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée"));

        // Vérifier les permissions d'accès
        checkInvoiceAccess(invoice);

        return details ? convertToDetailResponse(invoice) : convertToResponse(invoice);
    }


    @Override
    public InvoiceDetailResponse getInvoiceDetailsByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée"));

        // Vérifier les permissions d'accès
        checkInvoiceAccess(invoice);

        return convertToDetailResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getInvoicesByUser(Long userId) {
        // Vérification des permissions
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Vous n'êtes pas autorisé à accéder aux factures d'autres utilisateurs");
            }
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé");
        }

        return invoiceRepository.findByOrderUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByType(InvoiceType type) {
        if (securityUtil.isAdmin()) {
            // Les admins peuvent voir toutes les factures d'un type
            return invoiceRepository.findByType(type).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            // Les utilisateurs normaux ne peuvent voir que leurs factures
            User currentUser = securityUtil.getCurrentUser();
            return invoiceRepository.findByTypeAndOrderUserId(type, currentUser.getId()).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<InvoiceResponse> getInvoicesByTypeAndUser(InvoiceType type, Long userId) {
        // Vérification des permissions
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Vous n'êtes pas autorisé à accéder aux factures d'autres utilisateurs");
            }
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé");
        }

        return invoiceRepository.findByTypeAndOrderUserId(type, userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponse> getInvoicesByDateRange(LocalDateTime start, LocalDateTime end) {
        if (securityUtil.isAdmin()) {
            // Les admins peuvent voir toutes les factures dans une plage de dates
            return invoiceRepository.findByIssueDateBetween(start, end).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            // Les utilisateurs normaux ne peuvent voir que leurs factures
            User currentUser = securityUtil.getCurrentUser();
            return invoiceRepository.findByIssueDateBetweenAndOrderUserId(start, end, currentUser.getId()).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        if (securityUtil.isAdmin()) {
            // Les admins peuvent voir toutes les factures
            return invoiceRepository.findAll().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            // Les utilisateurs normaux ne peuvent voir que leurs factures
            User currentUser = securityUtil.getCurrentUser();
            return invoiceRepository.findByOrderUserId(currentUser.getId()).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<InvoiceResponse> searchInvoices(
            Long userId,
            InvoiceType type,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean isPaid,
            String invoiceNumber,
            String orderNumber) {

        // Vérification des permissions pour les recherches avec userId
        if (userId != null && !securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Vous n'êtes pas autorisé à accéder aux factures d'autres utilisateurs");
            }
        }

        // Si recherche par numéro de facture (priorité la plus haute)
        if (invoiceNumber != null) {
            return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                    .map(this::convertToResponse)
                    .map(List::of)
                    .orElse(List.of());
        }

        // Si recherche par numéro de commande
        if (orderNumber != null) {
            return invoiceRepository.findByOrderOrderNumber(orderNumber) 
            .flatMap(order -> invoiceRepository.findByOrderId(order.getId()))
                    .map(this::convertToResponse)
                    .map(List::of)
                    .orElse(List.of());
        }


        // Construction d'une recherche avec les filtres disponibles
        List<Invoice> invoices;

        if (userId != null && type != null) {
            invoices = invoiceRepository.findByTypeAndOrderUserId(type, userId);
        } else if (userId != null) {
            invoices = invoiceRepository.findByOrderUserId(userId);
        } else if (type != null) {
            if (securityUtil.isAdmin()) {
                invoices = invoiceRepository.findByType(type);
            } else {
                User currentUser = securityUtil.getCurrentUser();
                invoices = invoiceRepository.findByTypeAndOrderUserId(type, currentUser.getId());
            }
        } else if (startDate != null && endDate != null) {
            if (securityUtil.isAdmin()) {
                invoices = invoiceRepository.findByIssueDateBetween(startDate, endDate);
            } else {
                User currentUser = securityUtil.getCurrentUser();
                invoices = invoiceRepository.findByIssueDateBetweenAndOrderUserId(
                        startDate, endDate, currentUser.getId());
            }
        } else {
            // Pas de filtres spécifiques
            if (securityUtil.isAdmin()) {
                invoices = invoiceRepository.findAll();
            } else {
                User currentUser = securityUtil.getCurrentUser();
                invoices = invoiceRepository.findByOrderUserId(currentUser.getId());
            }
        }

        // Filtre additionnel pour isPaid (si spécifié)
        if (isPaid != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.isPaid() == isPaid)
                    .collect(Collectors.toList());
        }

        return invoices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generateInvoicePdf(Long id) {
        Invoice invoice = findAndCheckAccess(id);
        InvoiceDetailResponse invoiceDetail = convertToDetailResponse(invoice);
        return pdfGenerationService.generateInvoicePdf(invoiceDetail);
    }

    @Override
    @Transactional
    public InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest) {
        // Seuls les admins peuvent mettre à jour des factures
        if (!securityUtil.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seuls les administrateurs peuvent mettre à jour des factures");
        }

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée"));

        // Mise à jour des champs modifiables uniquement
        invoice.setBillingAddress(invoiceRequest.getBillingAddress());
        invoice.setShippingAddress(invoiceRequest.getShippingAddress());
        invoice.setNotes(invoiceRequest.getNotes());
        invoice.setTaxId(invoiceRequest.getTaxId());

        if (invoiceRequest.getTaxAmount() != null) {
            invoice.setTaxAmount(invoiceRequest.getTaxAmount());
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return convertToResponse(updatedInvoice);
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        // Seuls les admins peuvent supprimer des factures
        if (!securityUtil.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seuls les administrateurs peuvent supprimer des factures");
        }

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée"));

        // Désactiver la facture au lieu de la supprimer physiquement
        invoice.setActive(false);
        invoiceRepository.save(invoice);

        
        
        Order order = invoice.getOrder();
        order.setInvoice(null);
        orderRepository.save(order);

        // Supprimer la facture
        invoiceRepository.delete(invoice);
        
    }

    // Méthodes utilitaires
    private String generateInvoiceNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = LocalDateTime.now().format(formatter);
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "INV-" + datePart + "-" + randomPart;
    }

    private InvoiceResponse convertToResponse(Invoice invoice) {
        Order order = invoice.getOrder();
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getUser().getUsername())
                .issueDate(invoice.getIssueDate())
                .amount(invoice.getAmount())
                .isPaid(invoice.isPaid())
                .type(invoice.getType())
                .billingAddress(invoice.getBillingAddress())
                .shippingAddress(invoice.getShippingAddress())
                .dueDate(invoice.getDueDate())
                .taxId(invoice.getTaxId())
                .taxAmount(invoice.getTaxAmount())
                .build();
    }

    private InvoiceDetailResponse convertToDetailResponse(Invoice invoice) {
        Order order = invoice.getOrder();
        User user = order.getUser();
        Payment payment = order.getPayment();

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            itemResponses.add(OrderItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .build());
        }

        return InvoiceDetailResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(user.getUsername())
                .customerEmail(user.getEmail())
                .issueDate(invoice.getIssueDate())
                .amount(invoice.getAmount())
                .isPaid(invoice.isPaid())
                .type(invoice.getType())
                .billingAddress(invoice.getBillingAddress())
                .shippingAddress(invoice.getShippingAddress())
                .notes(invoice.getNotes())
                .dueDate(invoice.getDueDate())
                .taxId(invoice.getTaxId())
                .taxAmount(invoice.getTaxAmount())
                .totalWithTax(invoice.getAmount() + invoice.getTaxAmount())
                .items(itemResponses)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .transactionId(payment != null ? payment.getTransactionId() : null)
                .build();
    }

    private Invoice findAndCheckAccess(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée"));

        // Vérifier les permissions d'accès
        checkInvoiceAccess(invoice);

        return invoice;
    }

    private void checkInvoiceAccess(Invoice invoice) {
        // Les admins peuvent accéder à toutes les factures
        if (securityUtil.isAdmin()) {
            return;
        }

        // Les utilisateurs peuvent accéder uniquement à leurs propres factures
        User currentUser = securityUtil.getCurrentUser();
        if (!invoice.getOrder().getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Vous n'êtes pas autorisé à accéder à cette facture");
        }
    }
}