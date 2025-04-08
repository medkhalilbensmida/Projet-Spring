package tn.fst.spring.projet_spring.entities.sale;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.fst.spring.projet_spring.entities.payment.Invoice;
import tn.fst.spring.projet_spring.entities.payment.Payment;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private SaleType saleType; // ONLINE, DOOR_TO_DOOR
    
    private LocalDateTime saleDate;
    
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleItem> items;
    
    private Long customerId;
    
    private String customerName;
    
    private String customerAddress;
    
    private String customerPhone;
    
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    private SaleStatus status;
    
    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
    private Payment payment;
    
    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
    private Invoice invoice;
}