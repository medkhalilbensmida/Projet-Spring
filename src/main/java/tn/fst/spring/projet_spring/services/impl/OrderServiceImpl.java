@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    // ... existing fields and other methods ...

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        // ... existing updateOrderStatus logic ...
    }

    @Override
    @Transactional
    public OrderResponse createExchangeOrder(
            Long originalOrderId,
            Long complaintId,
            List<OrderItemRequest> itemsToShip,
            String reason
    ) {
        Order originalOrder = orderRepository.findById(originalOrderId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Original order not found with id: " + originalOrderId
            ));
        User user = originalOrder.getUser();

        Order exchangeOrder = new Order();
        exchangeOrder.setUser(user);
        exchangeOrder.setOrderDate(LocalDateTime.now());
        exchangeOrder.setStatus(OrderStatus.PROCESSING);
        exchangeOrder.setOrderNumber(generateOrderNumber());
        exchangeOrder.setSaleType(SaleType.ONLINE);
        exchangeOrder.setCustomerAddress(originalOrder.getCustomerAddress());
        exchangeOrder.setCustomerPhone(originalOrder.getCustomerPhone());
        exchangeOrder.setSalespersonNote(
            "Exchange for order " + originalOrder.getOrderNumber()
            + ". Complaint ID: " + complaintId
            + ". Reason: " + reason
        );

        if (itemsToShip == null || itemsToShip.isEmpty()) {
            throw new IllegalStateException("List of items to ship for exchange cannot be empty.");
        }

        for (OrderItemRequest itemRequest : itemsToShip) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found with id: " + itemRequest.getProductId()
                ));
            Stock stock = product.getStock();
            if (stock == null) {
                throw new IllegalStateException("Stock record missing for product: " + product.getName());
            }
            if (stock.getQuantity() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Not enough stock for exchange item: " + product.getName()
                    + ". Requested: " + itemRequest.getQuantity()
                    + ", Available: " + stock.getQuantity()
                );
            }
            // decrement stock
            stock.updateQuantity(-itemRequest.getQuantity());
            stockRepository.save(stock);

            OrderItem exchangeItem = new OrderItem();
            exchangeItem.setOrder(exchangeOrder);
            exchangeItem.setProduct(product);
            exchangeItem.setQuantity(itemRequest.getQuantity());
            exchangeItem.setUnitPrice(0.0);
            exchangeOrder.getItems().add(exchangeItem);
        }

        exchangeOrder.calculateTotal();
        Order savedExchangeOrder = orderRepository.save(exchangeOrder);
        return convertToResponse(savedExchangeOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Enhanced validation for order status
        if (order.getStatus() == OrderStatus.CONFIRMED ||
            order.getStatus() == OrderStatus.SHIPPED ||
            order.getStatus() == OrderStatus.DELIVERED
        ) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Order cannot be cancelled because it is already " + order.getStatus()
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Order is already cancelled."
            );
        }

        // Only owner or admin can cancel
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(order.getUser().getId())) {
                throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to cancel this order"
                );
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


    @Override
    @Transactional
    public OrderResponse createExchangeOrder(Long originalOrderId, Long complaintId, List<OrderItemRequest> itemsToShip, String reason) {
        Order originalOrder = orderRepository.findById(originalOrderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Original order not found with id: " + originalOrderId));
        User user = originalOrder.getUser();

        Order exchangeOrder = new Order();
        exchangeOrder.setUser(user);
        exchangeOrder.setOrderDate(LocalDateTime.now());
        exchangeOrder.setStatus(OrderStatus.PROCESSING); // Start as processing
        exchangeOrder.setOrderNumber(generateOrderNumber());
        exchangeOrder.setSaleType(SaleType.ONLINE); // Exchanges are typically online/internal
        exchangeOrder.setCustomerAddress(originalOrder.getCustomerAddress()); // Use original address
        exchangeOrder.setCustomerPhone(originalOrder.getCustomerPhone());
        exchangeOrder.setSalespersonNote("Exchange for order " + originalOrder.getOrderNumber() + ". Complaint ID: " + complaintId + ". Reason: " + reason);

        if (itemsToShip == null || itemsToShip.isEmpty()) {
            throw new IllegalStateException("List of items to ship for exchange cannot be empty.");
        }

        for (OrderItemRequest itemRequest : itemsToShip) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + itemRequest.getProductId()));
            Stock stock = product.getStock();
            if (stock == null) {
                throw new IllegalStateException("Stock record missing for product: " + product.getName());
            }
            if (stock.getQuantity() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for exchange item: " + product.getName() + ". Requested: " + itemRequest.getQuantity() + ", Available: " + stock.getQuantity());
            }
            // Decrease stock for the item being sent out in the exchange
            stock.updateQuantity(-itemRequest.getQuantity());
            stockRepository.save(stock);

            OrderItem exchangeItem = new OrderItem();
            exchangeItem.setOrder(exchangeOrder);
            exchangeItem.setProduct(product);
            exchangeItem.setQuantity(itemRequest.getQuantity());
            exchangeItem.setUnitPrice(0.0); // Exchanges typically have zero price for the items
            exchangeOrder.getItems().add(exchangeItem);
        }

        exchangeOrder.calculateTotal(); // Total should be 0.0 for exchange items
        Order savedExchangeOrder = orderRepository.save(exchangeOrder);
        return convertToResponse(savedExchangeOrder);
    }

    private String generateOrderNumber() {
        // Combinaison des deux méthodes de génération
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = LocalDateTime.now().format(formatter);
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
    private boolean needsAutomaticInvoice(Order order) {
        return order.getSaleType() == SaleType.ONLINE &&
                order.getStatus() == OrderStatus.CONFIRMED &&
                order.getPayment() != null &&
                order.getPayment().isSuccessful() &&
                order.getInvoice() == null;
    }

}

