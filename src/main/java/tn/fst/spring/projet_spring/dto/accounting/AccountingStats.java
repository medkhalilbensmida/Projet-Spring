package tn.fst.spring.projet_spring.dto.accounting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountingStats {
    private double totalSales;
    private double totalSalaries;
    private double totalDeliveryCosts;
    private double profit;
}