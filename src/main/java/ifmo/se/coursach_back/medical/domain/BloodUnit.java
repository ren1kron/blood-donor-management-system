package ifmo.se.coursach_back.medical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "blood_unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "component_type_id", nullable = false)
    private BloodComponentType componentType;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "rh_factor")
    private String rhFactor;

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @Column(name = "collected_at", nullable = false)
    private OffsetDateTime collectedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "storage_location")
    private String storageLocation;

    @PrePersist
    public void prePersist() {
        if (collectedAt == null) {
            collectedAt = OffsetDateTime.now();
        }
        if (status == null) {
            status = "IN_STOCK";
        }
    }
}
