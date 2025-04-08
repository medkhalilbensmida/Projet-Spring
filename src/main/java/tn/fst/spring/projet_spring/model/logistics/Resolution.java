package tn.fst.spring.projet_spring.model.logistics;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
public class Resolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResolutionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResolutionStatus status;

    @OneToOne
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;
}

