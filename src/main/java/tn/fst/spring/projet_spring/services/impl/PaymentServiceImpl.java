package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.fst.spring.projet_spring.dto.payment.PaymentRequest;
import tn.fst.spring.projet_spring.dto.payment.PaymentResponse;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.order.SaleType;
import tn.fst.spring.projet_spring.model.payment.Payment;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;
import tn.fst.spring.projet_spring.repositories.payment.PaymentRepository;
import tn.fst.spring.projet_spring.security.SecurityUtil;
import tn.fst.spring.projet_spring.services.interfaces.IInvoiceService;
import tn.fst.spring.projet_spring.services.interfaces.IPaymentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final IInvoiceService invoiceService;


    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // Check if payment already exists for this order
        if (paymentRepository.findByOrderId(paymentRequest.getOrderId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Payment already exists for this order");
        }

        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Security check: ensure customer can only make payments for their own orders
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!order.getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to make payments for this order"
                );
            }
        }

        // NEW: Check order status before allowing payment
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot process payment for order with status: " + order.getStatus() +
                            ". Only orders with PENDING status can be paid.");
        }

        // Enhanced validation - payment amount must match order total amount
        if (Math.abs(paymentRequest.getAmount() - order.getTotalAmount()) > 0.01) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payment amount does not match order total. Expected: " + order.getTotalAmount() +
                            ", Received: " + paymentRequest.getAmount());
        }

        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);
        payment.setCardLastFourDigits(paymentRequest.getCardLastFourDigits());
        payment.setPaymentProviderReference(paymentRequest.getPaymentProviderReference());
        payment.setNotes(paymentRequest.getNotes());
        payment.setSuccessful(true);

        Payment savedPayment = paymentRepository.save(payment);

        // Sync with order
        order.attachPayment(savedPayment);
        orderRepository.save(order);

        // Generate invoice automatically for online orders
        if (order.getSaleType() == SaleType.ONLINE) {
            try {
                invoiceService.generateInvoiceForOrder(order.getId());
            } catch (Exception e) {
                // Log the error but don't fail the payment
                log.error("Failed to generate invoice for order {}: {}",
                        order.getOrderNumber(), e.getMessage());
            }
        }

        return convertToResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        // Security check: ensure customer can only access their own payments
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!payment.getOrder().getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to access this payment"
                );
            }
        }

        return convertToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        // Security check: ensure customer can only access their own payments
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to access payments for this user"
                );
            }
        }

        // If userId exists, validate that it corresponds to a valid user
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }

        // Retrieve payments for the user
        List<Payment> payments = paymentRepository.findByOrderUserId(userId);

        // Convert to response DTOs
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        // Security check: ensure customer can only access their own payments
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!payment.getOrder().getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to access this payment"
                );
            }
        }

        return convertToResponse(payment);
    }


    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Security check: ensure customer can only access their own orders' payments
        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!order.getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to access payments for this order"
                );
            }
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Payment not found for this order"));

        return convertToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        if (securityUtil.isAdmin()) {
            // Admin can see all payments
            return paymentRepository.findAll().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            // Customers can only see their own payments
            User currentUser = securityUtil.getCurrentUser();
            return paymentRepository.findByOrderUserId(currentUser.getId()).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }
    @Override
    public List<PaymentResponse> getMyPayments() {
        User currentUser = securityUtil.getCurrentUser();
        return paymentRepository.findByOrderUserId(currentUser.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByPaymentMethod(PaymentMethod paymentMethod) {
        if (securityUtil.isAdmin()) {
            // Admin can see all payments by method
            return paymentRepository.findByPaymentMethod(paymentMethod).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            // Customers can only see their own payments by method
            User currentUser = securityUtil.getCurrentUser();
            return paymentRepository.findByPaymentMethodAndOrderUserId(paymentMethod, currentUser.getId()).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<PaymentResponse> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        if (securityUtil.isAdmin()) {
            // Admin can see all payments in date range
            return paymentRepository.findByPaymentDateBetween(start, end).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            // Customers can only see their own payments in date range
            User currentUser = securityUtil.getCurrentUser();
            return paymentRepository.findByPaymentDateBetweenAndOrderUserId(start, end, currentUser.getId()).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(boolean successful) {
        if (securityUtil.isAdmin()) {
            // Admin can see all payments by status
            return paymentRepository.findBySuccessful(successful).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            // Customers can only see their own payments by status
            User currentUser = securityUtil.getCurrentUser();
            return paymentRepository.findBySuccessfulAndOrderUserId(successful, currentUser.getId()).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        // Security check: only admin can delete payments
        if (!securityUtil.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only administrators can delete payments"
            );
        }

        // Remove the reference from order to avoid orphaned references
        Order order = payment.getOrder();
        order.setPayment(null);
        orderRepository.save(order);

        // Now it's safe to delete the payment
        paymentRepository.delete(payment);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private PaymentResponse convertToResponse(Payment payment) {
        Order order = payment.getOrder();
        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getUser().getUsername())
                .cardLastFourDigits(payment.getCardLastFourDigits())
                .paymentProviderReference(payment.getPaymentProviderReference())
                .successful(payment.isSuccessful())
                .notes(payment.getNotes())
                .build();
    }
}