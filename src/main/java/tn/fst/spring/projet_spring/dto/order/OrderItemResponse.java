package tn.fst.spring.projet_spring.dto.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;
}