package tn.fst.spring.projet_spring.entities.logistics;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tn.fst.spring.projet_spring.entities.auth.User;
import tn.fst.spring.projet_spring.entities.order.Order;

@Data
@Entity
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;

    @OneToOne(mappedBy = "complaint", cascade = CascadeType.ALL)
    private Resolution resolution;

    public void processComplaint() {
        this.status = ComplaintStatus.IN_PROGRESS;
    }
}