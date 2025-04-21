package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.statUserProduct.*;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.catalog.Product;
import tn.fst.spring.projet_spring.model.donation.Donation;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import tn.fst.spring.projet_spring.model.order.OrderItem;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.donation.DonationRepository;
import tn.fst.spring.projet_spring.repositories.logistics.DeliveryRequestRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderItemRepository;
import tn.fst.spring.projet_spring.repositories.products.ProductRepository;
import tn.fst.spring.projet_spring.services.interfaces.IAdvancedStatsService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvancedStatsServiceImpl implements IAdvancedStatsService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DonationRepository donationRepository;
    private final DeliveryRequestRepository deliveryRequestRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public UserBehaviorStatsDTO getUserBehaviorStats() {
        List<User> activeUsers = userRepository.findByIsActiveTrue();
        UserBehaviorStatsDTO dto = new UserBehaviorStatsDTO();
        dto.setAvgSessionsPerUser(calculateAvgDaysActive(activeUsers));
        dto.setAvgSessionDuration(calculateAvgActiveDuration(activeUsers));
        dto.setUserSegmentation(calculateUserSegmentation(activeUsers));
        dto.setRetentionRates(calculateRetentionRates());
        dto.setCohortAnalysis(calculateCohortAnalysis(activeUsers));
        return dto;
    }

    @Override
    public ProductPerformanceDTO getProductPerformanceStats() {
        List<OrderItem> items = orderItemRepository.findAll();
        Map<Product, Long> sales = items.stream()
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.summingLong(OrderItem::getQuantity)
                ));

        ProductPerformanceDTO dto = new ProductPerformanceDTO();
        dto.setTopSellingProducts(getTopProducts(sales, 5));
        dto.setWorstSellingProducts(getBottomProducts(sales, 5));
        dto.setAbcAnalysis(performABCAnalysis(sales));
        dto.setStockTurnover(calculateStockTurnover());
        dto.setPriceElasticity(Collections.emptyMap());
        return dto;
    }

    @Override
    public DonationAnalyticsDTO getDonationAnalytics() {
        List<Donation> dons = donationRepository.findAll();
        DonationAnalyticsDTO dto = new DonationAnalyticsDTO();
        dto.setDonationTrendLast30Days(calculateDonationTrend(dons, 30));
        dto.setDonationTrendLast90Days(calculateDonationTrend(dons, 90));
        dto.setTopDonors(calculateTopDonors(dons, 5));
        dto.setMostDonatedProducts(calculateMostDonatedProducts(dons, 5));
        dto.setSeasonalAnalysis(calculateSeasonalDonationPatterns(dons));
        return dto;
    }

    @Override
    public DeliveryAnalyticsDTO getDeliveryAnalytics() {
        List<DeliveryRequest> deliveries = deliveryRequestRepository.findAll();
        DeliveryAnalyticsDTO dto = new DeliveryAnalyticsDTO();
        dto.setDeliverySuccessRate(calculateDeliverySuccessRate(deliveries));
        dto.setLivreurPerformance(calculateLivreurPerformance(deliveries));
        dto.setLateDeliveriesAnalysis(Collections.emptyMap());
        return dto;
    }

    // --- User Behavior Helpers ---

    private Map<String, Double> calculateAvgDaysActive(List<User> users) {
        LocalDateTime now = LocalDateTime.now();
        double avg = users.stream()
                .mapToDouble(u -> {
                    LocalDateTime c = u.getCreatedAt();
                    return (c != null)
                            ? Duration.between(c, now).toDays()
                            : 0;
                })
                .average()
                .orElse(0);
        return Map.of("averageDaysActive", avg);
    }

    private Map<String, String> calculateAvgActiveDuration(List<User> users) {
        LocalDateTime now = LocalDateTime.now();
        double avgMin = users.stream()
                .mapToDouble(u -> {
                    LocalDateTime c = u.getCreatedAt();
                    return (c != null)
                            ? Duration.between(c, now).toMinutes()
                            : 0;
                })
                .average()
                .orElse(0);
        return Map.of("avg", String.format("%.0f min", avgMin));
    }

    private Map<String, Double> calculateUserSegmentation(List<User> users) {
        long total = users.size();
        Map<String, Long> counts = users.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getRole().getName(),
                        Collectors.counting()
                ));
        return counts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() * 100.0 / total
                ));
    }

    private Map<String, Double> calculateRetentionRates() {
        long total  = userRepository.count();
        long active = userRepository.findByIsActiveTrue().size();
        double rate = (total > 0) ? active * 100.0 / total : 0;
        return Map.of("retentionRate", rate);
    }

    private Map<String, Map<String, Double>> calculateCohortAnalysis(List<User> users) {
        long total = users.size();
        return users.stream()
                .filter(u -> u.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().getMonth().toString(),
                        Collectors.groupingBy(
                                u -> u.getRole().getName(),
                                Collectors.counting()
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        ent -> ent.getValue() * 100.0 / total
                                ))
                ));
    }

    // --- Product Performance Helpers ---

    private Map<String, Double> calculateStockTurnover() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null
                                ? p.getCategory().getName()
                                : "UNCATEGORIZED",
                        Collectors.summingDouble(p -> {
                            if (p.getStock() != null) {
                                return p.getStock().getQuantity();
                            }
                            return 0;
                        })
                ));
    }

    // --- Donation Analytics Helpers ---

    private Map<String, Long> calculateDonationTrend(List<Donation> donations, int days) {
        LocalDate end   = LocalDate.now();
        LocalDate start = end.minusDays(days);
        Map<LocalDate, Long> raw = donations.stream()
                .filter(d -> d.getEvent() != null && d.getEvent().getEventDate() != null)
                .filter(d -> !d.getEvent().getEventDate().toLocalDate().isBefore(start))
                .collect(Collectors.groupingBy(
                        d -> d.getEvent().getEventDate().toLocalDate(),
                        Collectors.summingLong(Donation::getQuantity)
                ));
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));
    }

    private List<DonationAnalyticsDTO.TopDonor> calculateTopDonors(List<Donation> donations, int limit) {
        return donations.stream()
                .filter(d -> d.getDonor() != null)
                .collect(Collectors.groupingBy(
                        Donation::getDonor,
                        Collectors.summingLong(Donation::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new DonationAnalyticsDTO.TopDonor(
                        e.getKey().getUsername(),
                        e.getValue()
                ))
                .collect(Collectors.toList());
    }

    private List<DonationAnalyticsDTO.MostDonatedProduct> calculateMostDonatedProducts(List<Donation> donations, int limit) {
        return donations.stream()
                .filter(d -> d.getProduct() != null)
                .collect(Collectors.groupingBy(
                        Donation::getProduct,
                        Collectors.summingLong(Donation::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Product, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new DonationAnalyticsDTO.MostDonatedProduct(
                        e.getKey().getName(),
                        e.getValue(),
                        e.getKey().getCategory() != null
                                ? e.getKey().getCategory().getName()
                                : "UNCATEGORIZED"
                ))
                .collect(Collectors.toList());
    }

    private Map<String, Double> calculateSeasonalDonationPatterns(List<Donation> donations) {
        Map<String, Long> bySeason = donations.stream()
                .filter(d -> d.getEvent() != null && d.getEvent().getEventDate() != null)
                .collect(Collectors.groupingBy(
                        d -> {
                            int m = d.getEvent().getEventDate().toLocalDate().getMonthValue();
                            if (m >= 3 && m <= 5)  return "spring";
                            if (m >= 6 && m <= 8)  return "summer";
                            if (m >= 9 && m <= 11) return "autumn";
                            return "winter";
                        },
                        Collectors.summingLong(Donation::getQuantity)
                ));
        long tot = bySeason.values().stream().mapToLong(Long::longValue).sum();
        return bySeason.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> tot > 0 ? e.getValue() * 100.0 / tot : 0
                ));
    }

    // --- Delivery Analytics Helpers ---

    private double calculateDeliverySuccessRate(List<DeliveryRequest> deliveries) {
        long tot = deliveries.size();
        long ok  = deliveries.stream()
                .filter(d -> d.getStatus() == DeliveryStatus.DELIVERED)
                .count();
        return tot > 0 ? ok * 100.0 / tot : 0;
    }

    private Map<String, Double> calculateLivreurPerformance(List<DeliveryRequest> deliveries) {
        long tot = deliveries.size();
        return deliveries.stream()
                .filter(d -> d.getLivreur() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getLivreur().getNom(),     // <-- on récupère ici le nom du livreur
                        Collectors.counting()
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> tot > 0 ? e.getValue() / (double) tot : 0
                ));
    }



    // --- ABC & utilities ---

    private List<ProductPerformanceDTO.ProductSales> getTopProducts(
            Map<Product, Long> sales, int limit) {
        return sales.entrySet().stream()
                .sorted(Map.Entry.<Product, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new ProductPerformanceDTO.ProductSales(
                        e.getKey().getName(), e.getValue(), e.getKey().getPrice()))
                .collect(Collectors.toList());
    }

    private List<ProductPerformanceDTO.ProductSales> getBottomProducts(
            Map<Product, Long> sales, int limit) {
        return sales.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .map(e -> new ProductPerformanceDTO.ProductSales(
                        e.getKey().getName(), e.getValue(), e.getKey().getPrice()))
                .collect(Collectors.toList());
    }

    private Map<String, Double> performABCAnalysis(Map<Product, Long> sales) {
        long total = sales.values().stream().mapToLong(Long::longValue).sum();
        return sales.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getName(),
                        e -> total > 0 ? e.getValue() * 100.0 / total : 0
                ));
    }
}
