package tn.fst.spring.projet_spring.dto.statistics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DailyRevenueResponse {
    private List<LocalDate> dates;
    private List<Double> revenues;
    private List<Integer> orderCounts;
}