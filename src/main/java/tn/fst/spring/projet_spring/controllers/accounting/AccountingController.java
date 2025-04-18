package tn.fst.spring.projet_spring.controllers.accounting;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.fst.spring.projet_spring.dto.accounting.AccountingStats;
import tn.fst.spring.projet_spring.services.interfaces.IAccountingService;

@RestController
@RequestMapping("/api/accounting")
@RequiredArgsConstructor
public class AccountingController {
    private final IAccountingService accountingService;

    @Operation(summary = "Récupérer les statistiques comptables globales de la boutique")
    @GetMapping("/stats")
    public ResponseEntity<AccountingStats> getAccountingStats() {
        return ResponseEntity.ok(accountingService.getAccountingStats());
    }
}