package ifmo.se.coursach_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lab_examination_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabExaminationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private Visit visit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by_staff_id", nullable = false)
    private StaffProfile requestedBy;

    @Column(name = "requested_at", nullable = false)
    private OffsetDateTime requestedAt;

    @ManyToOne
    @JoinColumn(name = "completed_by_lab_id")
    private StaffProfile completedByLab;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "hemoglobin_g_l")
    private BigDecimal hemoglobinGl;

    @Column(name = "hematocrit_pct")
    private BigDecimal hematocritPct;

    @Column(name = "rbc_10e12_l")
    private BigDecimal rbc10e12L;

    @Column(name = "systolic_mmhg")
    private Integer systolicMmhg;

    @Column(name = "diastolic_mmhg")
    private Integer diastolicMmhg;

    @Column(name = "pulse_rate")
    private Integer pulseRate;

    @Column(name = "body_temperature_c")
    private BigDecimal bodyTemperatureC;

    @PrePersist
    public void prePersist() {
        if (requestedAt == null) {
            requestedAt = OffsetDateTime.now();
        }
        if (status == null) {
            status = LabExaminationStatus.REQUESTED;
        }
    }
}
