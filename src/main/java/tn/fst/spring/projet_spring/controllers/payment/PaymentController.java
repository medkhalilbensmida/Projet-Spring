package tn.fst.spring.projet_spring.controllers.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.payment.PaymentRequest;
import tn.fst.spring.projet_spring.dto.payment.PaymentResponse;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;
import tn.fst.spring.projet_spring.services.interfaces.IPaymentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management API")
public class PaymentController {
    private final IPaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process a new payment")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        return new ResponseEntity<>(paymentService.processPayment(paymentRequest), HttpStatus.CREATED);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    @GetMapping
    @Operation(summary = "Get payments with optional filters")
    public ResponseEntity<List<PaymentResponse>> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Boolean successful) {

        // Handle different filtering scenarios
        if (userId != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
        } else if (orderId != null) {
            return ResponseEntity.ok(List.of(paymentService.getPaymentByOrderId(orderId)));
        } else if (paymentMethod != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByPaymentMethod(paymentMethod));
        } else if (startDate != null && endDate != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByDateRange(startDate, endDate));
        } else if (successful != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByStatus(successful));
        } else {
            return ResponseEntity.ok(paymentService.getAllPayments());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    /*@GetMapping("/my-payments")
    @Operation(summary = "Get current user's payments")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getMyPayments());
    }*/
}