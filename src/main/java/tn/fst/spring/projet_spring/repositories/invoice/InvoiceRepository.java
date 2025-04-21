package tn.fst.spring.projet_spring.repositories.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.invoice.Invoice;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;
import tn.fst.spring.projet_spring.model.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByOrderUserId(Long userId);
    List<Invoice> findByType(InvoiceType type);
    List<Invoice> findByTypeAndOrderUserId(InvoiceType type, Long userId);
    List<Invoice> findByIssueDateBetween(LocalDateTime start, LocalDateTime end);
    List<Invoice> findByIssueDateBetweenAndOrderUserId(LocalDateTime start, LocalDateTime end, Long userId);
    boolean existsByOrderId(Long orderId);
    Optional<Invoice> findByOrderId(Long orderId);
    List<Invoice> findByIsPaid(boolean isPaid);
    List<Invoice> findByIsPaidAndOrderUserId(boolean isPaid, Long userId);
    // pour rechercher par numéro de commande dans l'objet Order associé
    @org.springframework.data.jpa.repository.Query("SELECT i FROM Invoice i JOIN i.order o WHERE o.orderNumber = :orderNumber")
    Optional<Invoice> findByOrderOrderNumber(String orderNumber);}