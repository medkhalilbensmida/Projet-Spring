package tn.fst.spring.projet_spring.dto.statistics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesByTypeResponse {
    private int totalOnlineOrders;
    private int totalDoorToDoorOrders;
    private double onlineRevenue;
    private double doorToDoorRevenue;
    private double onlinePercentage;
    private double doorToDoorPercentage;
}