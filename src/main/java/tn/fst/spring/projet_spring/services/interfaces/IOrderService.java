package tn.fst.spring.projet_spring.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.fst.spring.projet_spring.dto.order.OrderRequest;
import tn.fst.spring.projet_spring.dto.order.OrderResponse;
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
    OrderResponse cancelOrder(Long id);

    List<OrderResponse> findOrdersBySaleTypeOptimized(SaleType saleType);
    Page<OrderResponse> findOrdersBySaleTypePaginated(SaleType saleType, Pageable pageable);
}