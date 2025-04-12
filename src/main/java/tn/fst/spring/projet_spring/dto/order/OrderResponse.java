package tn.fst.spring.projet_spring.dto.order;

import lombok.Builder;
import lombok.Data;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;
import tn.fst.spring.projet_spring.model.order.SaleType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String customerName;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private SaleType saleType;
    private Boolean isPaid;
    private Boolean isInvoiced;
    private List<OrderItemResponse> items;
    private String customerAddress;
    private String customerPhone;
    private String salespersonNote;
}