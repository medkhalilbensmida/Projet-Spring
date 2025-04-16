package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.fst.spring.projet_spring.dto.payment.PaymentRequest;
import tn.fst.spring.projet_spring.dto.payment.PaymentResponse;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.payment.Payment;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;
import tn.fst.spring.projet_spring.repositories.payment.PaymentRepository;
import tn.fst.spring.projet_spring.services.interfaces.IPaymentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

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

        // Save payment and update order in a single transaction
        Payment savedPayment = paymentRepository.save(payment);
        
        // Sync with order using the new attachPayment method
        order.attachPayment(savedPayment);
        orderRepository.save(order);

        return convertToResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        return convertToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        return convertToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Payment not found for this order"));
        return convertToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByPaymentMethod(PaymentMethod paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentDateBetween(start, end).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(boolean successful) {
        return paymentRepository.findBySuccessful(successful).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        
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