package tn.fst.spring.projet_spring.repositories.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.order.SaleType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface SalesStatisticsRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    long countOrdersInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    Double calculateRevenueInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o.status as status, COUNT(o) as count FROM Order o WHERE o.orderDate BETWEEN :start AND :end GROUP BY o.status")
    List<Map<String, Object>> countOrdersByStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('DATE', o.orderDate) as date, SUM(o.totalAmount) as revenue, COUNT(o) as count FROM Order o " +
            "WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED' " +
            "GROUP BY FUNCTION('DATE', o.orderDate) ORDER BY FUNCTION('DATE', o.orderDate)")
    List<Map<String, Object>> getDailyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.category.name as category, SUM(oi.quantity * oi.unitPrice) as revenue, SUM(oi.quantity) as quantity " +
            "FROM OrderItem oi JOIN oi.product p JOIN oi.order o " +
            "WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED' " +
            "GROUP BY p.category.name ORDER BY SUM(oi.quantity * oi.unitPrice) DESC")
    List<Map<String, Object>> getSalesByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Fix: Use actual table names from the database
    @Query(value = "SELECT p.id as productId, p.name as productName, c.name as category, SUM(oi.quantity) as quantity, SUM(oi.quantity * oi.unit_price) as revenue " +
            "FROM order_item oi " +
            "JOIN product p ON oi.product_id = p.id " +
            "JOIN category c ON p.category_id = c.id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.order_date BETWEEN :start AND :end AND o.status <> 'CANCELLED' " +
            "GROUP BY p.id, p.name, c.name " +
            "ORDER BY quantity DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Map<String, Object>> getTopSellingProducts(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("limit") int limit);

    @Query("SELECT CAST(o.saleType as string) as saleType, COUNT(o) as count, SUM(o.totalAmount) as revenue " +
        "FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED' " +
        "GROUP BY o.saleType")
List<Map<String, Object>> getSalesByType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :start AND :end")
    long countOrdersByStatusInPeriod(@Param("status") OrderStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(AVG(o.totalAmount), 0) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    double calculateAverageOrderValueInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.saleType = :saleType AND o.orderDate BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    long countOrdersBySaleTypeInPeriod(@Param("saleType") SaleType saleType, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}