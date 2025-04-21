package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.statUserProduct.*;

public interface IUserProductStatsService {
    UserStatsDTO getUserStats();
    ProductStatsDTO getProductStats();
    InventoryStatsDTO getInventoryStats();
    BarcodeStatsDTO getBarcodeStats();
}