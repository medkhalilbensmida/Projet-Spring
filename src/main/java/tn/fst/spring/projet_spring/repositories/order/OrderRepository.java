package tn.fst.spring.projet_spring.repositories.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.model.order.OrderStatus;
import tn.fst.spring.projet_spring.model.order.SaleType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserId(Long userId);
    List<Order> findBySaleType(SaleType saleType);
    List<Order> findBySaleTypeAndUserId(SaleType saleType, Long userId);
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByOrderDateBetweenAndUserId(LocalDateTime start, LocalDateTime end, Long userId);
    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
}