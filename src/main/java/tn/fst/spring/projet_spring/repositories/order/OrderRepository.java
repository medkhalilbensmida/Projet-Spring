package tn.fst.spring.projet_spring.repositories.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
    boolean existsByOrderNumber(String orderNumber);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = ?1")
    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(oi.quantity * oi.unitPrice), 0) " +
            "FROM Order o JOIN o.items oi " +
            "WHERE o.orderDate >= ?1 AND o.orderDate < ?2")
    Double getSalesBetween(LocalDateTime start, LocalDateTime end);

    default Double getTodaySales() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = LocalDateTime.now();
        return getSalesBetween(start, end);
    }
}