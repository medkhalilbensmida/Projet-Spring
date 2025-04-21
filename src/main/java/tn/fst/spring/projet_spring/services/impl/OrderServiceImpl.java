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

    // ... generateOrderNumber(), convertToResponse(), needsAutomaticInvoice(), etc. ...
}
