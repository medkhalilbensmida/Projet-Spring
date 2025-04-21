package tn.fst.spring.projet_spring.dto.statistics;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalesByPeriodRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer limit;
    private String categoryFilter;
}
