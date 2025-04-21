package tn.fst.spring.projet_spring.dto.invoice;

import lombok.Builder;
import lombok.Data;
import tn.fst.spring.projet_spring.dto.order.OrderItemResponse;
import tn.fst.spring.projet_spring.model.invoice.InvoiceType;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceDetailResponse {
    private Long id;
    private String invoiceNumber;
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private LocalDateTime issueDate;
    private double amount;
    private boolean isPaid;
    private InvoiceType type;
    private String billingAddress;
    private String shippingAddress;
    private String notes;
    private LocalDateTime dueDate;
    private String taxId;
    private double taxAmount;
    private double totalWithTax;
    private List<OrderItemResponse> items;
    private PaymentMethod paymentMethod;
    private String transactionId;
}