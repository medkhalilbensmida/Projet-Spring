package tn.fst.spring.projet_spring.entities.marketing;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String url;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private AdvertisementChannel channel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "targeted_audience_id")
    private TargetedAudience targetedAudience;   

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private double cost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdvertisementType type; // "image" ou "video"

    public enum AdvertisementType {
        IMAGE,
        VIDEO,
        TEXT,
    }

    private int views = 0;

    private int initialViews = 0;

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }
    }


}