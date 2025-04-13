package tn.fst.spring.projet_spring.controllers.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.order.OrderRequest;
import tn.fst.spring.projet_spring.dto.order.OrderResponse;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.order.SaleType;
import tn.fst.spring.projet_spring.services.interfaces.IOrderService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management API")
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.createOrder(orderRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping
    @Operation(summary = "Get all orders with optional filters")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) SaleType saleType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (userId != null && startDate != null && endDate != null) {
            return ResponseEntity.ok(orderService.findOrdersByUserAndDateRange(userId, startDate, endDate));
        } else if (startDate != null && endDate != null) {
            return ResponseEntity.ok(orderService.findOrdersByDateRange(startDate, endDate));
        } else if (saleType != null) {
            return ResponseEntity.ok(orderService.findOrdersBySaleType(saleType));
        } else if (userId != null) {
            return ResponseEntity.ok(orderService.getOrdersByUser(userId));
        } else {
            return ResponseEntity.ok(orderService.getAllOrders());
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order ")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order ")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}