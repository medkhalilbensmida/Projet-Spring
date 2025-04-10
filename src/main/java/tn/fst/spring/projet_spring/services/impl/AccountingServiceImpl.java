package tn.fst.spring.projet_spring.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.accounting.AccountingStats;
import tn.fst.spring.projet_spring.services.interfaces.IAccountingService;

@Service
@RequiredArgsConstructor
public class AccountingServiceImpl implements IAccountingService {
    @Override
    public AccountingStats getAccountingStats() {
        // Implémentation simplifiée - à compléter avec vos calculs réels
        double totalSales = 0; // Remplacer par le calcul réel
        double totalSalaries = 0; // Remplacer par le calcul réel
        double totalDeliveryCosts = 0; // Remplacer par le calcul réel
        double profit = totalSales - totalSalaries - totalDeliveryCosts;

        return AccountingStats.builder()
                .totalSales(totalSales)
                .totalSalaries(totalSalaries)
                .totalDeliveryCosts(totalDeliveryCosts)
                .profit(profit)
                .build();
    }
}