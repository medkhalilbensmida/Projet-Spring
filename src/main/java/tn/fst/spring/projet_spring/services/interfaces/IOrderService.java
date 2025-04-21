package tn.fst.spring.projet_spring.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.fst.spring.projet_spring.dto.order.OrderRequest;
import tn.fst.spring.projet_spring.dto.order.OrderResponse;
import tn.fst.spring.projet_spring.dto.order.OrderItemRequest;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.order.SaleType;

import java.time.LocalDateTime;
import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse getOrderById(Long id);
    OrderResponse getOrderByNumber(String orderNumber);
    List<OrderResponse> getOrdersByUser(Long userId);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    void deleteOrder(Long id);
    List<OrderResponse> findOrdersBySaleType(SaleType saleType);
    List<OrderResponse> findOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    List<OrderResponse> findOrdersByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);



    // Methods from HEAD
    OrderResponse cancelOrder(Long id);
    List<OrderResponse> findOrdersBySaleTypeOptimized(SaleType saleType);
    Page<OrderResponse> findOrdersBySaleTypePaginated(SaleType saleType, Pageable pageable);


    // Method from FAdi
    /**
     * Creates a new order representing an exchange based on an original order and complaint.
     * @param originalOrderId The ID of the order that led to the complaint.
     * @param complaintId The ID of the complaint justifying the exchange.
     * @param itemsToShip List of items (product ID and quantity) to be shipped in the exchange.
     * @param reason Description/reason for the exchange.
     * @return OrderResponse for the newly created exchange order.
     * @throws tn.fst.spring.projet_spring.exception.ResourceNotFoundException if original order, user, or products are not found.
     * @throws IllegalStateException if stock is insufficient for items to ship.
     */
    OrderResponse createExchangeOrder(Long originalOrderId, Long complaintId, List<OrderItemRequest> itemsToShip, String reason);

}