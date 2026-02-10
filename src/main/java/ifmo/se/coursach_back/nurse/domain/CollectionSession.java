package ifmo.se.coursach_back.nurse.domain;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "collection_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private Visit visit;

    @ManyToOne
    @JoinColumn(name = "nurse_staff_id")
    private StaffProfile nurse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionSessionStatus status;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "pre_systolic_mmhg")
    private Integer preSystolicMmhg;

    @Column(name = "pre_diastolic_mmhg")
    private Integer preDiastolicMmhg;

    @Column(name = "pre_pulse_rate")
    private Integer prePulseRate;

    @Column(name = "pre_body_temperature_c")
    private BigDecimal preBodyTemperatureC;

    @Column(name = "pre_wellbeing")
    private String preWellbeing;

    @Column(name = "post_systolic_mmhg")
    private Integer postSystolicMmhg;

    @Column(name = "post_diastolic_mmhg")
    private Integer postDiastolicMmhg;

    @Column(name = "post_pulse_rate")
    private Integer postPulseRate;

    @Column(name = "post_body_temperature_c")
    private BigDecimal postBodyTemperatureC;

    @Column(name = "post_wellbeing")
    private String postWellbeing;

    @Column(name = "notes")
    private String notes;

    @Column(name = "complications")
    private String complications;

    @Column(name = "interruption_reason")
    private String interruptionReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = CollectionSessionStatus.PREPARED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
