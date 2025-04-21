package tn.fst.spring.projet_spring.dto.statUserProduct;

import lombok.Data;

@Data
public class InventoryStatsDTO {
    private long totalItemsInStock;
    private int outOfStockProducts;
    private int productsNearThreshold;
    private double inventoryValue;
    private StockInfo mostStockedProduct;
    private StockInfo leastStockedProduct;

    @Data
    public static class StockInfo {
        private String name;
        private int quantity;

        public StockInfo() {}

        public StockInfo(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }
}