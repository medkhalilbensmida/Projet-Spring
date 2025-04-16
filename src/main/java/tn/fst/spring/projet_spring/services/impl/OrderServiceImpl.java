package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.fst.spring.projet_spring.dto.order.*;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.catalog.Product;
import tn.fst.spring.projet_spring.model.catalog.Stock;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.order.OrderItem;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.order.SaleType;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;
import tn.fst.spring.projet_spring.repositories.products.ProductRepository;
import tn.fst.spring.projet_spring.repositories.products.StockRepository;
import tn.fst.spring.projet_spring.security.SecurityUtil;
import tn.fst.spring.projet_spring.services.interfaces.IOrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final SecurityUtil securityUtil;


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // For order creation, we need to ensure the user is creating the order for themselves
        User currentUser = securityUtil.getCurrentUser();
        User targetUser;

        if (orderRequest.getUserId() != null) {
            // If userId is specified, check if it's the same as current user or the user is admin
            if (!securityUtil.isAdmin() && !currentUser.getId().equals(orderRequest.getUserId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to create orders for other users"
                );
            }
            targetUser = userRepository.findById(orderRequest.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        } else {
            // If userId is not specified, use the current user's ID
            targetUser = currentUser;
        }

        Order order = new Order();
        order.setUser(targetUser);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderNumber(generateOrderNumber());
        order.setSaleType(orderRequest.getSaleType());

        // Set door-to-door specific information if applicable
        if (orderRequest.getSaleType() == SaleType.DOOR_TO_DOOR) {
            order.setCustomerAddress(orderRequest.getCustomerAddress());
            order.setCustomerPhone(orderRequest.getCustomerPhone());
            order.setSalespersonNote(orderRequest.getSalespersonNote());
        }

        // Add items and update stock
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found with id: " + itemRequest.getProductId()));

            // Check stock availability
            Stock stock = product.getStock();
            if (stock.getQuantity() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Not enough stock for product: " + product.getName());
            }

            // Update stock
            stock.updateQuantity(-itemRequest.getQuantity());
            stockRepository.save(stock);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            order.getItems().add(orderItem);
        }

        order.calculateTotal();
        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Check if user can access this order
        securityUtil.checkOrderOwnership(order);

        return convertToResponse(order);
    }




    @Override
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Check if user can access this order
        securityUtil.checkOrderOwnership(order);

        return convertToResponse(order);
    }


    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {
        // If not admin and querying other user's orders, throw exception
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to access orders for other users"
                );
            }
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        // This method is already secured at controller level to admin only
        return orderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Only admins can update order status
        if (!securityUtil.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only administrators can update order status"
            );
        }

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToResponse(updatedOrder);
    }


    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Only admins can delete orders
        if (!securityUtil.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only administrators can delete orders"
            );
        }

        // Return items to stock
        for (OrderItem item : order.getItems()) {
            Stock stock = item.getProduct().getStock();
            stock.updateQuantity(item.getQuantity());
            stockRepository.save(stock);
        }

        orderRepository.delete(order);
    }


    @Override
    public List<OrderResponse> findOrdersBySaleType(SaleType saleType) {
        if (securityUtil.isAdmin()) {
            return orderRepository.findBySaleType(saleType)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            User currentUser = securityUtil.getCurrentUser();
            return orderRepository.findBySaleTypeAndUserId(saleType, currentUser.getId())
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<OrderResponse> findOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        if (securityUtil.isAdmin()) {
            return orderRepository.findByOrderDateBetween(start, end)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            User currentUser = securityUtil.getCurrentUser();
            return orderRepository.findByOrderDateBetweenAndUserId(start, end, currentUser.getId())
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<OrderResponse> findOrdersByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        // If not admin and querying other user's orders, throw exception
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to access orders for other users"
                );
            }
        }

        return orderRepository.findByUserIdAndOrderDateBetween(userId, start, end)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Enhanced validation for order status
        if (order.getStatus() == OrderStatus.CONFIRMED ||
                order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order cannot be cancelled because it is already " + order.getStatus());
        }

        // Check if order is already cancelled
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order is already cancelled.");
        }

        // Check if authenticated user is the owner of the order or has admin role
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(order.getUser().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You are not authorized to cancel this order");
            }
        }

        // Return items to stock
        for (OrderItem item : order.getItems()) {
            Stock stock = item.getProduct().getStock();
            stock.updateQuantity(item.getQuantity());
            stockRepository.save(stock);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        return convertToResponse(updatedOrder);
    }


    private String generateOrderNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = LocalDateTime.now().format(formatter);
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "ORD-" + datePart + "-" + randomPart;
    }

    private OrderResponse convertToResponse(Order order) {
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

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getUser().getUsername())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .saleType(order.getSaleType())
                .isPaid(order.getPayment() != null)
                .isInvoiced(order.getInvoice() != null)
                .items(itemResponses)
                .customerAddress(order.getCustomerAddress())
                .customerPhone(order.getCustomerPhone())
                .salespersonNote(order.getSalespersonNote())
                .build();
    }


}