package tn.fst.spring.projet_spring.entities.logistics;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isAvailable;

    @OneToMany(mappedBy = "deliveryPerson")
    private Set<DeliveryRequest> deliveryRequests = new HashSet<>();

    public void updateAvailability(boolean available) {
        this.isAvailable = available;
    }
}