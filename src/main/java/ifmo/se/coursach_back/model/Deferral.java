package ifmo.se.coursach_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deferral")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deferral {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private DonorProfile donor;

    @ManyToOne
    @JoinColumn(name = "created_from_check_id")
    private MedicalCheck createdFromCheck;

    @Enumerated(EnumType.STRING)
    @Column(name = "deferral_type", nullable = false)
    private DeferralType deferralType;

    @Column(nullable = false)
    private String reason;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @PrePersist
    public void prePersist() {
        if (startsAt == null) {
            startsAt = OffsetDateTime.now();
        }
    }
}
