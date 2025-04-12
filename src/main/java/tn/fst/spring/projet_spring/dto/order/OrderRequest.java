package tn.fst.spring.projet_spring.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;
import tn.fst.spring.projet_spring.model.order.SaleType;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull
    private Long userId;

    @NotNull
    private SaleType saleType;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotEmpty
    private List<OrderItemRequest> items;

    // For door-to-door sales
    private String customerAddress;
    private String customerPhone;
    private String salespersonNote;
}