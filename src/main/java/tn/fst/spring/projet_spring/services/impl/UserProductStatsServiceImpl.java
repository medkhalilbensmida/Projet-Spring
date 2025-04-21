package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.statUserProduct.*;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.catalog.Product;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.products.ProductRepository;
import tn.fst.spring.projet_spring.repositories.products.StockRepository;
import tn.fst.spring.projet_spring.services.interfaces.IUserProductStatsService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProductStatsServiceImpl implements IUserProductStatsService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    public UserStatsDTO getUserStats() {
        UserStatsDTO dto = new UserStatsDTO();

        // Statistiques de base
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        dto.setTotalUsers(totalUsers);
        dto.setActiveUsers(activeUsers);
        dto.setInactiveUsers(totalUsers - activeUsers);

        // Répartition par rôle
        Map<String, Long> usersByRole = new LinkedHashMap<>();
        usersByRole.put("ROLE_ADMIN", userRepository.countByRoleName("ROLE_ADMIN"));
        usersByRole.put("ROLE_CUSTOMER", userRepository.countByRoleName("ROLE_CUSTOMER"));
        usersByRole.put("ROLE_PRODUCT_MANAGER", userRepository.countByRoleName("ROLE_PRODUCT_MANAGER"));
        usersByRole.put("ROLE_SHELF_MANAGER", userRepository.countByRoleName("ROLE_SHELF_MANAGER"));
        usersByRole.put("ROLE_DELIVERY_MANAGER", userRepository.countByRoleName("ROLE_DELIVERY_MANAGER"));
        usersByRole.put("ROLE_EVENT_MANAGER", userRepository.countByRoleName("ROLE_EVENT_MANAGER"));
        dto.setUsersByRole(usersByRole);

        // Nouvelles inscriptions
        long newUsersLast7Days = userRepository.countNewUsersLast7Days();
        long newUsersLast30Days = userRepository.countNewUsersLast30Days();
        dto.setNewUsersLast7Days(newUsersLast7Days);
        dto.setNewUsersLast30Days(newUsersLast30Days);
        dto.setAvgUserRegistrationPerDay(newUsersLast30Days > 0 ?
                newUsersLast30Days / 30.0 : 0);

        // Activité utilisateur
        UserStatsDTO.UserActivityStats activityStats = new UserStatsDTO.UserActivityStats();
        activityStats.setRecentlyUpdatedToday(userRepository.countActiveToday());
        activityStats.setRecentlyUpdatedLast7Days(userRepository.countActiveLast7Days());
        activityStats.setRecentlyUpdatedLast30Days(userRepository.countActiveLast30Days());
        activityStats.setMostActiveTimeRange(calculateMostActiveHour());
        dto.setActivityStats(activityStats);

        // Inscriptions par jour de la semaine
        Map<String, Long> registrationsByDay = initializeDayMap();
        userRepository.countUsersByDayOfWeekLast30Days().forEach((dayOfWeek, count) -> {
            registrationsByDay.put(convertDayOfWeek(dayOfWeek), count);
        });
        dto.setUserRegistrationsByDay(registrationsByDay);

        return dto;
    }

    private String calculateMostActiveHour() {
        try {
            Map<Integer, Long> activityByHour = userRepository.countActivitiesByHourLast30Days();
            return activityByHour.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> String.format("%02dh-%02dh", entry.getKey(), entry.getKey()+1))
                    .orElse("Inconnu");
        } catch (Exception e) {
            log.warn("Erreur calcul heure active", e);
            return "10h-12h";
        }
    }

    private Map<String, Long> initializeDayMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("Lundi", 0L);
        map.put("Mardi", 0L);
        map.put("Mercredi", 0L);
        map.put("Jeudi", 0L);
        map.put("Vendredi", 0L);
        map.put("Samedi", 0L);
        map.put("Dimanche", 0L);
        return map;
    }

    private String convertDayOfWeek(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 2 -> "Lundi"; case 3 -> "Mardi"; case 4 -> "Mercredi";
            case 5 -> "Jeudi"; case 6 -> "Vendredi"; case 7 -> "Samedi";
            case 1 -> "Dimanche"; default -> "Inconnu";
        };
    }

    @Override
    public ProductStatsDTO getProductStats() {
        ProductStatsDTO dto = new ProductStatsDTO();
        List<Product> allProducts = productRepository.findAll();

        // Statistiques de base
        dto.setTotalProducts(allProducts.size());

        // Par catégorie
        Map<String, Long> productsByCategory = allProducts.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().getName(),
                        Collectors.counting()
                ));
        dto.setProductsByCategory(productsByCategory);

        // Stock
        dto.setProductsBelowStockThreshold(safeLongToInt(stockRepository.countByQuantityLessThan(5)));
        dto.setAvgPrice(productRepository.getAveragePrice() != null ?
                productRepository.getAveragePrice() : 0.0);

        // Produits extrêmes
        productRepository.findTopByOrderByPriceDesc()
                .ifPresent(p -> dto.setMostExpensiveProduct(createProductInfo(p)));
        productRepository.findTopByOrderByPriceAsc()
                .ifPresent(p -> dto.setCheapestProduct(createProductInfo(p)));

        // Distribution des prix
        ProductStatsDTO.ProductPriceDistribution priceDistribution = new ProductStatsDTO.ProductPriceDistribution();
        priceDistribution.setUnder10(countProductsInPriceRange(allProducts, 0, 10));
        priceDistribution.setFrom10to50(countProductsInPriceRange(allProducts, 10, 50));
        priceDistribution.setFrom50to100(countProductsInPriceRange(allProducts, 50, 100));
        priceDistribution.setOver100(countProductsInPriceRange(allProducts, 100, Double.MAX_VALUE));
        dto.setPriceDistribution(priceDistribution);

        // Catégories populaires
        setPopularCategories(dto, productsByCategory);

        return dto;
    }

    private ProductStatsDTO.ProductInfo createProductInfo(Product p) {
        ProductStatsDTO.ProductInfo info = new ProductStatsDTO.ProductInfo();
        info.setName(p.getName());
        info.setPrice(p.getPrice());
        info.setCategory(p.getCategory().getName());
        return info;
    }

    private int countProductsInPriceRange(List<Product> products, double min, double max) {
        return (int) products.stream()
                .filter(p -> p.getPrice() >= min && p.getPrice() < max)
                .count();
    }

    private void setPopularCategories(ProductStatsDTO dto, Map<String, Long> productsByCategory) {
        productsByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> dto.setMostPopularCategory(createCategoryStats(entry)));

        productsByCategory.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .ifPresent(entry -> dto.setLeastPopularCategory(createCategoryStats(entry)));
    }

    private ProductStatsDTO.CategoryStats createCategoryStats(Map.Entry<String, Long> entry) {
        ProductStatsDTO.CategoryStats stats = new ProductStatsDTO.CategoryStats();
        stats.setName(entry.getKey());
        stats.setProductCount(entry.getValue());
        stats.setAvgPrice(productRepository.getAveragePriceByCategory(entry.getKey()) != null ?
                productRepository.getAveragePriceByCategory(entry.getKey()) : 0.0);
        return stats;
    }

    @Override
    public InventoryStatsDTO getInventoryStats() {
        InventoryStatsDTO dto = new InventoryStatsDTO();

        // Statistiques de stock
        Long totalStock = stockRepository.sumQuantity();
        dto.setTotalItemsInStock(totalStock != null ? totalStock : 0L);
        dto.setOutOfStockProducts(safeLongToInt(stockRepository.countByQuantity(0)));
        dto.setProductsNearThreshold(safeLongToInt(stockRepository.countByQuantityLessThan(10)));

        // Valeur de l'inventaire
        Double inventoryValue = productRepository.calculateTotalInventoryValue();
        dto.setInventoryValue(inventoryValue != null ? inventoryValue : 0.0);

        // Produits les plus/moins stockés
        stockRepository.findTopByOrderByQuantityDesc()
                .ifPresent(s -> dto.setMostStockedProduct(
                        new InventoryStatsDTO.StockInfo(
                                s.getProduct().getName(),
                                safeLongToInt(s.getQuantity()))
                ));

        stockRepository.findTopByOrderByQuantityAsc()
                .ifPresent(s -> dto.setLeastStockedProduct(
                        new InventoryStatsDTO.StockInfo(
                                s.getProduct().getName(),
                                safeLongToInt(s.getQuantity()))
                ));

        return dto;
    }

    @Override
    public BarcodeStatsDTO getBarcodeStats() {
        BarcodeStatsDTO dto = new BarcodeStatsDTO();
        List<Product> products = productRepository.findAll();

        // Statistiques de base
        long totalWithBarcode = products.stream()
                .filter(p -> p.getBarcode() != null && !p.getBarcode().isEmpty())
                .count();
        dto.setTotalProductsWithBarcode(safeLongToInt(totalWithBarcode));

        long tunisianProducts = products.stream()
                .filter(p -> p.getBarcode() != null && p.getBarcode().startsWith("619"))
                .count();
        dto.setTotalTunisianProducts(safeLongToInt(tunisianProducts));

        dto.setTunisianProductsPercentage(totalWithBarcode > 0 ?
                (tunisianProducts * 100.0 / totalWithBarcode) : 0);

        // Analyse par préfixe
        Map<String, Long> productsByPrefix = products.stream()
                .filter(p -> p.getBarcode() != null && p.getBarcode().length() >= 3)
                .collect(Collectors.groupingBy(
                        p -> p.getBarcode().substring(0, 3),
                        Collectors.counting()
                ));
        dto.setProductsByBarcodePrefix(productsByPrefix);

        // Analyse par longueur
        Map<String, Long> productsByLength = products.stream()
                .filter(p -> p.getBarcode() != null)
                .collect(Collectors.groupingBy(
                        p -> String.valueOf(p.getBarcode().length()),
                        Collectors.counting()
                ));
        dto.setProductsByBarcodeLength(productsByLength);

        // Valeurs les plus communes
        productsByPrefix.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> dto.setMostCommonBarcodePrefix(entry.getKey()));

        productsByLength.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> dto.setMostCommonBarcodeLength(Integer.parseInt(entry.getKey())));

        return dto;
    }

    private int safeLongToInt(long value) {
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }
}