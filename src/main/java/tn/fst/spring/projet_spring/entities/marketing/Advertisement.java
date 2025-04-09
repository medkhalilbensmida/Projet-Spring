package tn.fst.spring.projet_spring.entities.marketing;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvertisementChannel channel;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private double cost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvertisementType type;

    @OneToOne(mappedBy = "advertisement", cascade = CascadeType.ALL)
    private TargetAudience targetAudience;

    public void trackPerformance() {
        // Implementation of performance tracking logic
    }
}

