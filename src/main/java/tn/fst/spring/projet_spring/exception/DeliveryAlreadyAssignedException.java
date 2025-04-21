package tn.fst.spring.projet_spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Delivery request already has a livreur assigned.")
public class DeliveryAlreadyAssignedException extends RuntimeException {
    public DeliveryAlreadyAssignedException(String message) {
        super(message);
    }
} 