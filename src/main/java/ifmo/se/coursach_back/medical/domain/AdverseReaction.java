package ifmo.se.coursach_back.medical.domain;
import ifmo.se.coursach_back.admin.domain.StaffProfile;

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
@Table(name = "adverse_reaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdverseReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @ManyToOne
    @JoinColumn(name = "reported_by_staff_id")
    private StaffProfile reportedBy;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column
    @Enumerated(EnumType.STRING)
    private ReactionSeverity severity;

    @Column
    private String description;

    @PrePersist
    public void prePersist() {
        if (occurredAt == null) {
            occurredAt = OffsetDateTime.now();
        }
    }
}
